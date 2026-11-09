package com.auth.api.framework.auth.adapter.port.in.web;

import com.auth.api.core.auth.application.port.in.mfa.AdminResetMfaUseCase;
import com.auth.api.core.shared.enumerated.Roles;
import com.auth.api.framework.auth.adapter.dto.AdminMfaResetRequestValidator;
import com.auth.api.framework.auth.adapter.dto.response.AdminMfaResetResponseDTO;
import com.auth.api.framework.shared.config.security.annotations.RequiredRole;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/users")
@Tag(name = "Admin MFA Controller")
public class AdminMfaController {

    private final AdminResetMfaUseCase adminResetMfaUseCase;

    public AdminMfaController(AdminResetMfaUseCase adminResetMfaUseCase) {
        this.adminResetMfaUseCase = adminResetMfaUseCase;
    }

    @PostMapping("/{userId}/mfa/reset")
    @RequiredRole({Roles.ADMIN})
    @Operation(
            summary = "Reset a user's MFA (admin)",
            description = "For users who lost both the authenticator and the recovery codes. Only run it after "
                    + "verifying the user's identity out of band. Sets MFA back to PENDING, deletes the TOTP "
                    + "secret, recovery codes and pending challenges, revokes all sessions and emails the user. "
                    + "Requires the X-XSRF-TOKEN header (CSRF)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "MFA reset"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid reason or admin trying to reset own MFA",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Not authenticated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Not an admin",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<ControllerResponseDTO<AdminMfaResetResponseDTO>> resetMfa(
            @PathVariable String userId,
            @Valid @RequestBody AdminMfaResetRequestValidator request,
            Authentication authentication
    ) {
        AdminMfaResetResponseDTO body = AdminMfaResetResponseDTO.from(
                adminResetMfaUseCase.execute(authentication.getName(), userId, request.reason())
        );

        return ResponseEntity.ok(new ControllerResponseDTO<>(
                "MFA reset. The user must set up a new authenticator on the next sign in",
                body
        ));
    }
}
