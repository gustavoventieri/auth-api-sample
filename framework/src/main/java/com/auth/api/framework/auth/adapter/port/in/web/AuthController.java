package com.auth.api.framework.auth.adapter.port.in.web;

import com.auth.api.core.auth.application.dto.response.MfaChallengeResponse;
import com.auth.api.core.auth.application.port.in.sign.SignInUseCase;
import com.auth.api.core.auth.application.port.in.sign.SignUpUseCase;
import com.auth.api.core.auth.application.port.in.emailVerification.VerifyEmailVerificationTokenUseCase;
import com.auth.api.framework.auth.adapter.dto.EmailVerificationRequestValidator;
import com.auth.api.framework.auth.adapter.dto.SignInRequestValidator;
import com.auth.api.framework.auth.adapter.dto.SignUpRequestValidator;
import com.auth.api.framework.auth.adapter.dto.response.MfaChallengeResponseDTO;
import com.auth.api.framework.shared.dto.response.ControllerResponseDTO;
import com.auth.api.framework.shared.handler.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication Controller")
public class AuthController {

    private final SignInUseCase signInUseCase;
    private final SignUpUseCase signUpUseCase;
    private final VerifyEmailVerificationTokenUseCase verifyEmailVerificationTokenUseCase;

    public AuthController(
            SignInUseCase signInUseCase,
            SignUpUseCase signUpUseCase,
            VerifyEmailVerificationTokenUseCase verifyEmailVerificationTokenUseCase
    ) {
        this.signInUseCase = signInUseCase;
        this.signUpUseCase = signUpUseCase;
        this.verifyEmailVerificationTokenUseCase =
                verifyEmailVerificationTokenUseCase;
    }

    @PostMapping("/sign-in")
    @Operation(
            summary = "Sign in (step 1 of 2)",
            description = "Validate email and password. No session is created: the response carries an mfaToken "
                    + "and a status telling the next step (MFA_SETUP_REQUIRED or MFA_REQUIRED)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Password valid, MFA challenge issued"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Email not verified",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Invalid credentials",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    public ResponseEntity<ControllerResponseDTO<MfaChallengeResponseDTO>> signIn(
            @Valid @RequestBody SignInRequestValidator request
    ) {

        MfaChallengeResponse response = signInUseCase.execute(
                request.email(),
                request.password()
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ControllerResponseDTO<>(
                        "MFA required to complete sign in",
                        MfaChallengeResponseDTO.from(response)
                ));
    }

    @PostMapping("/sign-up")
    @Operation(
            summary = "Sign Up",
            description = "Create a user and send a verification code."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User created and OTP sent"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Email already registered",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    public ResponseEntity<ControllerResponseDTO<String>> signup(
            @Valid @RequestBody SignUpRequestValidator request
    ) {

        String message = signUpUseCase.execute(
                request.name(),
                request.email(),
                request.password()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ControllerResponseDTO<>(
                        message,
                        null
                ));
    }

    @PostMapping("/verify-email")
    @Operation(
            summary = "Verify email",
            description = "Verify the user's email. No session is created: the response carries an mfaToken "
                    + "with status MFA_SETUP_REQUIRED so the user can link an authenticator app."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Email verified, MFA setup challenge issued"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Verification code invalid, expired or already used",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    public ResponseEntity<ControllerResponseDTO<MfaChallengeResponseDTO>> verifyEmail(
            @Valid @RequestBody EmailVerificationRequestValidator request
    ) {

        MfaChallengeResponse response =
                verifyEmailVerificationTokenUseCase.execute(
                        request.codeOtp(),
                        request.email()
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ControllerResponseDTO<>(
                        "Email verified successfully. Set up MFA to continue",
                        MfaChallengeResponseDTO.from(response)
                ));
    }
}
