package com.auth.api.framework.auth.adapter.port.in.web;

import com.auth.core.auth.application.dto.response.AuthResponse;
import com.auth.core.auth.application.port.in.sign.SignInUseCase;
import com.auth.core.auth.application.port.in.sign.SignUpUseCase;
import com.auth.core.auth.application.port.in.sign.VerifyEmailVerificationTokenUseCase;
import com.auth.api.framework.auth.adapter.dto.EmailVerificationRequestValidator;
import com.auth.api.framework.auth.adapter.dto.SignInRequestValidator;
import com.auth.api.framework.auth.adapter.dto.SignUpRequestValidator;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication Controller")
public class AuthController {

    private static final String ACCESS_TOKEN_NAME = "access_token";
    private static final String REFRESH_TOKEN_NAME = "refresh_token";

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
            summary = "Sign in",
            description = "Authenticate a user and create a new session."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Authenticated successfully"
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
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    public ResponseEntity<ControllerResponseDTO<String>> signIn(
            HttpServletResponse servletResponse,
            @Valid @RequestBody SignInRequestValidator request,
            HttpServletRequest httpRequest
    ) {

        String ip = getClientIp(httpRequest);
        String device = getDevice(httpRequest);

        AuthResponse response = signInUseCase.execute(
                request.email(),
                request.password(),
                ip,
                device
        );

        addAuthCookies(servletResponse, response);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ControllerResponseDTO<>(
                        "Login successful",
                        null
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
            description = "Verify the user's email and create a session."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Email verified successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Verification code expired or already used",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid verification code",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    public ResponseEntity<ControllerResponseDTO<String>> verifyEmail(
            HttpServletResponse servletResponse,
            @Valid @RequestBody EmailVerificationRequestValidator request,
            HttpServletRequest httpRequest
    ) {

        String ip = getClientIp(httpRequest);
        String device = getDevice(httpRequest);

        AuthResponse response =
                verifyEmailVerificationTokenUseCase.execute(
                        request.codeOtp(),
                        request.email(),
                        ip,
                        device
                );

        addAuthCookies(servletResponse, response);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ControllerResponseDTO<>(
                        "Email verified successfully",
                        null
                ));
    }

    private String getDevice(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    private String getClientIp(HttpServletRequest request) {

        String forwarded = request.getHeader("X-Forwarded-For");

        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }

        return request.getRemoteAddr();
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
}