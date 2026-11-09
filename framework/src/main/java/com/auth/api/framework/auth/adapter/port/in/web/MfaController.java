package com.auth.api.framework.auth.adapter.port.in.web;

import com.auth.api.core.auth.application.dto.response.AuthResponse;
import com.auth.api.core.auth.application.dto.response.MfaSetupCompletedResponse;
import com.auth.api.core.auth.application.port.in.mfa.ConfirmTotpSetupUseCase;
import com.auth.api.core.auth.application.port.in.mfa.SetupTotpUseCase;
import com.auth.api.core.auth.application.port.in.mfa.VerifyRecoveryCodeUseCase;
import com.auth.api.core.auth.application.port.in.mfa.VerifyTotpUseCase;
import com.auth.api.framework.auth.adapter.dto.MfaCodeRequestValidator;
import com.auth.api.framework.auth.adapter.dto.MfaRecoveryCodeRequestValidator;
import com.auth.api.framework.auth.adapter.dto.MfaTokenRequestValidator;
import com.auth.api.framework.auth.adapter.dto.response.RecoveryCodesResponseDTO;
import com.auth.api.framework.auth.adapter.dto.response.TotpSetupResponseDTO;
import com.auth.api.framework.auth.adapter.port.in.web.support.AuthCookieWriter;
import com.auth.api.framework.auth.adapter.port.in.web.support.ClientRequestInfo;
import com.auth.api.framework.shared.dto.response.ControllerResponseDTO;
import com.auth.api.framework.shared.handler.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Segunda etapa do login (MFA TOTP obrigatório). Todos os endpoints são autenticados
 * pelo mfaToken devolvido em /auth/sign-in ou /auth/verify-email.
 */
@RestController
@RequestMapping("/auth/mfa")
@Tag(name = "MFA Controller")
public class MfaController {

    private final SetupTotpUseCase setupTotpUseCase;
    private final ConfirmTotpSetupUseCase confirmTotpSetupUseCase;
    private final VerifyTotpUseCase verifyTotpUseCase;
    private final VerifyRecoveryCodeUseCase verifyRecoveryCodeUseCase;
    private final AuthCookieWriter authCookieWriter;

    public MfaController(
            SetupTotpUseCase setupTotpUseCase,
            ConfirmTotpSetupUseCase confirmTotpSetupUseCase,
            VerifyTotpUseCase verifyTotpUseCase,
            VerifyRecoveryCodeUseCase verifyRecoveryCodeUseCase,
            AuthCookieWriter authCookieWriter
    ) {
        this.setupTotpUseCase = setupTotpUseCase;
        this.confirmTotpSetupUseCase = confirmTotpSetupUseCase;
        this.verifyTotpUseCase = verifyTotpUseCase;
        this.verifyRecoveryCodeUseCase = verifyRecoveryCodeUseCase;
        this.authCookieWriter = authCookieWriter;
    }

    @PostMapping("/totp/setup")
    @Operation(
            summary = "Start TOTP setup",
            description = "Requires an mfaToken with status MFA_SETUP_REQUIRED. Generates a TOTP secret and returns "
                    + "the QR code / otpauth URI to be scanned by Google Authenticator, Authy, etc. "
                    + "Calling it again replaces the pending secret."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Secret generated"),
            @ApiResponse(
                    responseCode = "400",
                    description = "MFA token invalid, expired or MFA already enabled",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<ControllerResponseDTO<TotpSetupResponseDTO>> setup(
            @Valid @RequestBody MfaTokenRequestValidator request
    ) {
        TotpSetupResponseDTO body = TotpSetupResponseDTO.from(setupTotpUseCase.execute(request.mfaToken()));

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(new ControllerResponseDTO<>("Scan the QR code with your authenticator app", body));
    }

    @PostMapping("/totp/setup/confirm")
    @Operation(
            summary = "Confirm TOTP setup",
            description = "Validates the first code from the authenticator app, enables MFA, creates the session "
                    + "(cookies) and returns the recovery codes. Recovery codes are shown only once."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "MFA enabled and session created"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid code, invalid/expired MFA token or setup not started",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<ControllerResponseDTO<RecoveryCodesResponseDTO>> confirmSetup(
            @Valid @RequestBody MfaCodeRequestValidator request,
            HttpServletRequest httpRequest,
            HttpServletResponse servletResponse
    ) {
        MfaSetupCompletedResponse result = confirmTotpSetupUseCase.execute(
                request.mfaToken(),
                request.code(),
                ClientRequestInfo.ip(httpRequest),
                ClientRequestInfo.device(httpRequest)
        );

        authCookieWriter.addAuthCookies(servletResponse, result.authResponse());

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(new ControllerResponseDTO<>(
                        "MFA enabled. Store your recovery codes in a safe place",
                        new RecoveryCodesResponseDTO(result.recoveryCodes())
                ));
    }

    @PostMapping("/totp/verify")
    @Operation(
            summary = "Verify TOTP code (step 2 of 2)",
            description = "Requires an mfaToken with status MFA_REQUIRED. Validates the 6-digit code and creates the session."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authenticated successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid code or invalid/expired MFA token",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<ControllerResponseDTO<String>> verify(
            @Valid @RequestBody MfaCodeRequestValidator request,
            HttpServletRequest httpRequest,
            HttpServletResponse servletResponse
    ) {
        AuthResponse authResponse = verifyTotpUseCase.execute(
                request.mfaToken(),
                request.code(),
                ClientRequestInfo.ip(httpRequest),
                ClientRequestInfo.device(httpRequest)
        );

        authCookieWriter.addAuthCookies(servletResponse, authResponse);

        return ResponseEntity.ok(new ControllerResponseDTO<>("Login successful", null));
    }

    @PostMapping("/recovery")
    @Operation(
            summary = "Sign in with a recovery code",
            description = "Alternative to the TOTP code when the authenticator is lost. Each recovery code works once."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authenticated successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid recovery code or invalid/expired MFA token",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<ControllerResponseDTO<String>> recovery(
            @Valid @RequestBody MfaRecoveryCodeRequestValidator request,
            HttpServletRequest httpRequest,
            HttpServletResponse servletResponse
    ) {
        AuthResponse authResponse = verifyRecoveryCodeUseCase.execute(
                request.mfaToken(),
                request.recoveryCode(),
                ClientRequestInfo.ip(httpRequest),
                ClientRequestInfo.device(httpRequest)
        );

        authCookieWriter.addAuthCookies(servletResponse, authResponse);

        return ResponseEntity.ok(new ControllerResponseDTO<>("Login successful", null));
    }
}
