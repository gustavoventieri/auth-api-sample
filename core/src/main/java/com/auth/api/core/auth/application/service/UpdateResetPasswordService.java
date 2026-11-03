package com.auth.api.core.auth.application.service;

import com.auth.api.core.auth.application.port.in.password.reset.UpdateResetPasswordUseCase;
import com.auth.api.core.auth.application.port.in.password.HashPasswordUseCase;
import com.auth.api.core.auth.application.port.in.password.reset.HashResetPasswordVerificationTokenUseCase;

import com.auth.api.core.auth.application.port.out.ResetPasswordVerificationTokenPersistencePort;
import com.auth.api.core.auth.domain.ResetPasswordVerificationTokenDomain;
import com.auth.api.core.shared.email.EmailMessage;
import com.auth.api.core.shared.email.EmailPort;
import com.auth.api.core.shared.email.EmailTemplateUseCase;
import com.auth.api.core.shared.exception.BusinessException;
import com.auth.api.core.shared.exception.NotFoundException;
import com.auth.api.core.shared.transaction.TransactionManager;
import com.auth.api.core.user.application.port.out.UserAuthenticationPersistencePort;
import com.auth.api.core.user.application.port.out.UserRepositoryPort;
import com.auth.api.core.user.domain.UserDomain;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
@Slf4j
public class UpdateResetPasswordService implements UpdateResetPasswordUseCase {
    private final UserAuthenticationPersistencePort userAuthenticationPersistencePort;
    private final UserRepositoryPort userRepositoryPort;
    private final ResetPasswordVerificationTokenPersistencePort tokenPersistencePort;
    private final HashResetPasswordVerificationTokenUseCase hashTokenUseCase;
    private final HashPasswordUseCase hashPasswordUseCase;
    private final TransactionManager transactionManager;
    private final EmailPort emailPort;
    private final EmailTemplateUseCase emailTemplateUseCase;

    public UpdateResetPasswordService(
            UserAuthenticationPersistencePort userAuthenticationPersistencePort,
            UserRepositoryPort userRepositoryPort,
            ResetPasswordVerificationTokenPersistencePort tokenPersistencePort,
            HashResetPasswordVerificationTokenUseCase hashTokenUseCase,
            HashPasswordUseCase hashPasswordUseCase,
            TransactionManager transactionManager,
            EmailPort emailPort,
            EmailTemplateUseCase emailTemplateUseCase
    ) {
        this.userAuthenticationPersistencePort = userAuthenticationPersistencePort;
        this.userRepositoryPort = userRepositoryPort;
        this.tokenPersistencePort = tokenPersistencePort;
        this.hashTokenUseCase = hashTokenUseCase;
        this.hashPasswordUseCase = hashPasswordUseCase;
        this.transactionManager = transactionManager;
        this.emailPort = emailPort;
        this.emailTemplateUseCase = emailTemplateUseCase;
    }

    @Override
    public void execute(String email, String otpCode, String newPassword) {

        if (email == null || email.isBlank()) {
            throw new BusinessException("Email is empty");
        }

        if (otpCode == null || otpCode.isBlank()) {
            throw new BusinessException("Token is empty");
        }

        if (newPassword == null || newPassword.isBlank()) {
            throw new BusinessException("Password is empty");
        }

        UserDomain userDomain = userAuthenticationPersistencePort.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Something went wrong"));

        if (!userDomain.isActive() || !userDomain.isVerified()) {
            throw new BusinessException("Something went wrong");
        }

        String hashedOtp = hashTokenUseCase.execute(otpCode);

        ResetPasswordVerificationTokenDomain token = tokenPersistencePort
                .findResetPasswordVerificationTokenByCode(hashedOtp, email)
                .orElseThrow(() -> new BusinessException("Token is invalid"));

        if (token.isExpired() || token.isUsed()) {
            throw new BusinessException("Token is expired or was used");
        }

        boolean matches = MessageDigest.isEqual(
                hashedOtp.getBytes(StandardCharsets.UTF_8),
                token.otpCode().getBytes(StandardCharsets.UTF_8)
        );

        if (!matches) {
            throw new BusinessException("Token is invalid");
        }

        String newPasswordHash = hashPasswordUseCase.execute(newPassword);

        UserDomain updatedUser = userDomain.updatePassword(newPasswordHash);
        ResetPasswordVerificationTokenDomain usedToken = token.markAsUsed();

        transactionManager.execute(() -> {

            userRepositoryPort.save(updatedUser);
            tokenPersistencePort.save(usedToken);

            return null;
        });

        try {
            emailPort.send(
                    new EmailMessage(
                            updatedUser.email(),
                            "Sua senha foi alterada",
                            emailTemplateUseCase.buildPasswordChangedEmail(updatedUser.name())
                    )
            );
        } catch (Exception e) {
            log.warn("Failed to send password changed email for userId={}", updatedUser.id());
        }
    }
}
