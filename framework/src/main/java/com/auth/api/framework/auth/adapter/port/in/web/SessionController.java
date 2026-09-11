package com.auth.api.framework.auth.adapter.port.in.web;

import com.auth.core.auth.application.dto.response.AuthResponse;
import com.auth.core.auth.application.port.in.sign.RefreshTokenUseCase;
import com.auth.core.auth.application.port.in.sign.RevokeRefreshTokenUseCase;
import com.auth.core.auth.application.port.in.sign.SignOutUseCase;
import com.auth.api.framework.shared.dto.response.ControllerResponseDTO;
import com.auth.api.framework.shared.handler.dto.ErrorResponse;
import com.auth.core.shared.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/sessions")
@Tag(name = "Session Controller")
public class SessionController {

    private static final String ACCESS_TOKEN_NAME = "access_token";
    private static final String REFRESH_TOKEN_NAME = "refresh_token";

    private final RefreshTokenUseCase refreshTokenUseCase;
    private final RevokeRefreshTokenUseCase revokeRefreshTokenUseCase;
    private final SignOutUseCase signOutUseCase;

    public SessionController(
            RefreshTokenUseCase refreshTokenUseCase,
            RevokeRefreshTokenUseCase revokeRefreshTokenUseCase,
            SignOutUseCase signOutUseCase
    ) {
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.revokeRefreshTokenUseCase = revokeRefreshTokenUseCase;
        this.signOutUseCase = signOutUseCase;
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Refresh access token",
            description = "Generate a new access token using the current refresh token."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Access token refreshed successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid or expired refresh token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    public ResponseEntity<ControllerResponseDTO<String>> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        String refreshToken = getRefreshToken(request);

        try {

            AuthResponse authResponse =
                    refreshTokenUseCase.execute(refreshToken);

            addAuthCookies(response, authResponse);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ControllerResponseDTO<>(
                            "Token refreshed successfully",
                            null
                    ));

        } catch (BusinessException ex) {

            clearAuthCookies(response);

            throw ex;
        }
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Logout",
            description = "Revoke the current session and clear authentication cookies."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Logged out successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid refresh token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    public ResponseEntity<ControllerResponseDTO<String>> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        String refreshToken = getRefreshToken(request);

        try {

            signOutUseCase.execute(refreshToken);

        } finally {

            clearAuthCookies(response);
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ControllerResponseDTO<>(
                        "Logout successful",
                        null
                ));
    }

    @DeleteMapping("/revoke/{sessionId}")
    @Operation(
            summary = "Revoke session",
            description = "Revoke a specific session belonging to the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Session revoked successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Session not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    public ResponseEntity<ControllerResponseDTO<String>> revokeSession(
            @PathVariable String sessionId,
            Authentication authentication
    ) {

        String userId = authentication.getName();

        revokeRefreshTokenUseCase.execute(
                sessionId,
                userId
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ControllerResponseDTO<>(
                        "Session revoked successfully",
                        null
                ));
    }

    private String getRefreshToken(
            HttpServletRequest request
    ) {

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            throw new BusinessException(
                    "Refresh token not found"
            );
        }

        for (Cookie cookie : cookies) {

            if (REFRESH_TOKEN_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        throw new BusinessException(
                "Refresh token not found"
        );
    }

    private void addAuthCookies(
            HttpServletResponse response,
            AuthResponse authResponse
    ) {

        ResponseCookie accessTokenCookie = ResponseCookie
                .from(
                        ACCESS_TOKEN_NAME,
                        authResponse.accessToken()
                )
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofMinutes(15))
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie
                .from(
                        REFRESH_TOKEN_NAME,
                        authResponse.refreshToken()
                )
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofDays(30))
                .build();

        response.addHeader(
                "Set-Cookie",
                accessTokenCookie.toString()
        );

        response.addHeader(
                "Set-Cookie",
                refreshTokenCookie.toString()
        );
    }

    private void clearAuthCookies(
            HttpServletResponse response
    ) {

        ResponseCookie accessTokenCookie = ResponseCookie
                .from(ACCESS_TOKEN_NAME, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ZERO)
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie
                .from(REFRESH_TOKEN_NAME, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ZERO)
                .build();

        response.addHeader(
                "Set-Cookie",
                accessTokenCookie.toString()
        );

        response.addHeader(
                "Set-Cookie",
                refreshTokenCookie.toString()
        );
    }
}