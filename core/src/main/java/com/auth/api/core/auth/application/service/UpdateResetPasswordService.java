package com.auth.api.core.auth.application.service;

import com.auth.api.core.auth.application.port.in.password.HashPasswordUseCase;
import com.auth.api.core.auth.application.port.in.password.reset.UpdateResetPasswordUseCase;
import com.auth.api.core.auth.application.port.out.ResetPasswordVerificationTokenPersistencePort;
import com.auth.api.core.auth.application.service.ResetPasswordTokenValidator.ValidResetPasswordToken;
import com.auth.api.core.auth.domain.ResetPasswordVerificationTokenDomain;
import com.auth.api.core.shared.email.EmailMessage;
import com.auth.api.core.shared.email.EmailPort;
import com.auth.api.core.shared.email.EmailTemplateUseCase;
import com.auth.api.core.shared.transaction.TransactionManager;
import com.auth.api.core.user.application.port.out.UserAuthenticationPersistencePort;
import com.auth.api.core.user.domain.UserDomain;
import lombok.extern.slf4j.Slf4j;

import static com.auth.api.core.shared.util.ValidationUtils.requireNotBlank;

@Slf4j
public class UpdateResetPasswordService implements UpdateResetPasswordUseCase {

    private final ResetPasswordTokenValidator resetPasswordTokenValidator;
    private final UserAuthenticationPersistencePort userAuthenticationPersistencePort;
    private final ResetPasswordVerificationTokenPersistencePort tokenPersistencePort;
    private final HashPasswordUseCase hashPasswordUseCase;
    private final TransactionManager transactionManager;
    private final EmailPort emailPort;
    private final EmailTemplateUseCase emailTemplateUseCase;

    public UpdateResetPasswordService(
            ResetPasswordTokenValidator resetPasswordTokenValidator,
            UserAuthenticationPersistencePort userAuthenticationPersistencePort,
            ResetPasswordVerificationTokenPersistencePort tokenPersistencePort,
            HashPasswordUseCase hashPasswordUseCase,
            TransactionManager transactionManager,
            EmailPort emailPort,
            EmailTemplateUseCase emailTemplateUseCase
    ) {
        this.resetPasswordTokenValidator = resetPasswordTokenValidator;
        this.userAuthenticationPersistencePort = userAuthenticationPersistencePort;
        this.tokenPersistencePort = tokenPersistencePort;
        this.hashPasswordUseCase = hashPasswordUseCase;
        this.transactionManager = transactionManager;
        this.emailPort = emailPort;
        this.emailTemplateUseCase = emailTemplateUseCase;
    }

    @Override
    public void execute(String email, String otpCode, String newPassword) {
        requireNotBlank(newPassword, "Password is empty");

        ValidResetPasswordToken valid = resetPasswordTokenValidator.validate(email, otpCode);

        UserDomain updatedUser = valid.user().updatePassword(hashPasswordUseCase.execute(newPassword));
        ResetPasswordVerificationTokenDomain usedToken = valid.token().markAsUsed();

        transactionManager.run(() -> {
            userAuthenticationPersistencePort.save(updatedUser);
            tokenPersistencePort.save(usedToken);
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
            log.warn("Failed to send password changed email for userId={}", updatedUser.id(), e);
        }
    }
}
