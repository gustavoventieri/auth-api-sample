package com.auth.api.core.auth.application.service.password.reset;

import com.auth.api.core.auth.application.port.in.password.reset.GenerateResetPasswordVerificationTokenUseCase;
import com.auth.api.core.auth.application.port.in.password.reset.HashResetPasswordVerificationTokenUseCase;
import com.auth.api.core.auth.application.port.in.password.reset.SendResetPasswordVerificationTokenUseCase;
import com.auth.api.core.auth.application.port.out.ResetPasswordVerificationTokenPersistencePort;
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
public class SendResetPasswordVerificationTokenService implements SendResetPasswordVerificationTokenUseCase {

    private final ResetPasswordVerificationTokenPersistencePort resetPasswordVerificationTokenPersistencePort;
    private final UserAuthenticationPersistencePort userAuthenticationPersistencePort;
    private final TransactionManager transactionManager;
    private final GenerateResetPasswordVerificationTokenUseCase generateResetPasswordVerificationTokenUseCase;
    private final HashResetPasswordVerificationTokenUseCase hashResetPasswordVerificationTokenUseCase;
    private final EmailPort emailPort;
    private final EmailTemplateUseCase emailTemplateUseCase;

    public SendResetPasswordVerificationTokenService(
            ResetPasswordVerificationTokenPersistencePort resetPasswordVerificationTokenPersistencePort,
            UserAuthenticationPersistencePort userAuthenticationPersistencePort,
            TransactionManager transactionManager,
            GenerateResetPasswordVerificationTokenUseCase generateResetPasswordVerificationTokenUseCase,
            HashResetPasswordVerificationTokenUseCase hashResetPasswordVerificationTokenUseCase,
            EmailPort emailPort,
            EmailTemplateUseCase emailTemplateUseCase
    ) {
        this.resetPasswordVerificationTokenPersistencePort = resetPasswordVerificationTokenPersistencePort;
        this.userAuthenticationPersistencePort = userAuthenticationPersistencePort;
        this.transactionManager = transactionManager;
        this.generateResetPasswordVerificationTokenUseCase = generateResetPasswordVerificationTokenUseCase;
        this.hashResetPasswordVerificationTokenUseCase = hashResetPasswordVerificationTokenUseCase;
        this.emailPort = emailPort;
        this.emailTemplateUseCase = emailTemplateUseCase;
    }

    @Override
    public void execute(String email) {

        requireNotBlank(email, "Email is empty");

        UserDomain userDomain = userAuthenticationPersistencePort.findByEmail(email)
                .filter(UserDomain::isActiveAndVerified)
                .orElse(null);

        if (userDomain == null) {
            log.info("Reset password requested for unknown or ineligible email");
            return;
        }

        String otpCode = generateResetPasswordVerificationTokenUseCase.execute();
        String otpHash = hashResetPasswordVerificationTokenUseCase.execute(otpCode);

        ResetPasswordVerificationTokenDomain token =
                ResetPasswordVerificationTokenDomain.create(userDomain.id(), otpHash);

        transactionManager.run(() -> {
            resetPasswordVerificationTokenPersistencePort.deleteByUserId(userDomain.id());
            resetPasswordVerificationTokenPersistencePort.save(token);
        });

        emailPort.send(
                new EmailMessage(
                        userDomain.email(),
                        "Reset your password",
                        emailTemplateUseCase.buildResetPasswordEmail(otpCode)
                )
        );
    }
}