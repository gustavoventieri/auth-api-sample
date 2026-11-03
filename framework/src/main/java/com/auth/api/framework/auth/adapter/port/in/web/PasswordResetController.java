package com.auth.api.framework.auth.adapter.port.in.web;

import com.auth.api.core.auth.application.port.in.password.reset.SendResetPasswordVerificationTokenUseCase;
import com.auth.api.core.auth.application.port.in.password.reset.UpdateResetPasswordUseCase;
import com.auth.api.core.auth.application.port.in.password.reset.VerifyResetPasswordVerificationTokenUseCase;
import com.auth.api.framework.auth.adapter.dto.ForgotPasswordRequestValidator;
import com.auth.api.framework.auth.adapter.dto.ResetPasswordRequestValidator;
import com.auth.api.framework.auth.adapter.dto.VerifyResetPasswordRequestValidator;
import com.auth.api.framework.shared.dto.response.ControllerResponseDTO;
import com.auth.api.framework.shared.handler.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/password")
@Tag(name = "Password Reset Controller")
public class PasswordResetController {

    private static final String FORGOT_PASSWORD_MESSAGE =
            "If the email is registered, a reset code has been sent to it.";

    private final SendResetPasswordVerificationTokenUseCase sendResetPasswordVerificationTokenUseCase;
    private final VerifyResetPasswordVerificationTokenUseCase verifyResetPasswordVerificationTokenUseCase;
    private final UpdateResetPasswordUseCase updateResetPasswordUseCase;

    public PasswordResetController(
            SendResetPasswordVerificationTokenUseCase sendResetPasswordVerificationTokenUseCase,
            VerifyResetPasswordVerificationTokenUseCase verifyResetPasswordVerificationTokenUseCase,
            UpdateResetPasswordUseCase updateResetPasswordUseCase
    ) {
        this.sendResetPasswordVerificationTokenUseCase = sendResetPasswordVerificationTokenUseCase;
        this.verifyResetPasswordVerificationTokenUseCase = verifyResetPasswordVerificationTokenUseCase;
        this.updateResetPasswordUseCase = updateResetPasswordUseCase;
    }

    @PostMapping("/forgot")
    @Operation(
            summary = "Forgot password",
            description = "Send a password reset code to the user's email."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Request accepted"
            )
    })
    public ResponseEntity<ControllerResponseDTO<String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestValidator request
    ) {
        sendResetPasswordVerificationTokenUseCase.execute(request.email());

        return ResponseEntity.ok(new ControllerResponseDTO<>(FORGOT_PASSWORD_MESSAGE, null));
    }

    @PostMapping("/verify")
    @Operation(
            summary = "Verify reset code",
            description = "Check if the password reset code is valid."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Reset code is valid"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Reset code is invalid, expired or already used",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<ControllerResponseDTO<String>> verifyResetCode(
            @Valid @RequestBody VerifyResetPasswordRequestValidator request
    ) {
        verifyResetPasswordVerificationTokenUseCase.execute(request.codeOtp(), request.email());

        return ResponseEntity.ok(new ControllerResponseDTO<>("Reset code is valid", null));
    }

    @PostMapping("/reset")
    @Operation(
            summary = "Reset password",
            description = "Set a new password using a valid reset code."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Password updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Reset code is invalid, expired or already used",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<ControllerResponseDTO<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequestValidator request
    ) {
        updateResetPasswordUseCase.execute(request.email(), request.codeOtp(), request.newPassword());

        return ResponseEntity.ok(new ControllerResponseDTO<>("Password updated successfully", null));
    }
}
