package com.auth.api.core.auth.application.service;

import com.auth.api.core.auth.application.port.in.emailVerification.GenerateEmailVerificationTokenUseCase;
import com.auth.api.core.auth.application.port.in.emailVerification.HashEmailVerificationTokenUseCase;
import com.auth.api.core.auth.application.port.in.password.HashPasswordUseCase;
import com.auth.api.core.auth.application.port.in.sign.SignUpUseCase;
import com.auth.api.core.auth.application.port.out.EmailVerificationTokenPort;
import com.auth.api.core.auth.domain.EmailVerificationTokenDomain;
import com.auth.api.core.shared.email.EmailMessage;
import com.auth.api.core.shared.email.EmailTemplateUseCase;
import com.auth.api.core.shared.exception.BusinessException;
import com.auth.api.core.shared.email.EmailPort;
import com.auth.api.core.shared.transaction.TransactionManager;
import com.auth.api.core.user.application.port.out.UserAuthenticationPersistencePort;
import com.auth.api.core.user.domain.UserDomain;

public class SignUpService implements SignUpUseCase {

    private static final String SUCCESS_MESSAGE =
            "If the registration can proceed, a verification code has been sent to the provided email.";

    private final UserAuthenticationPersistencePort userAuthenticationPort;
    private final EmailVerificationTokenPort emailVerificationTokenRepositoryPort;

    private final HashPasswordUseCase hashPasswordUseCase;
    private final GenerateEmailVerificationTokenUseCase generateEmailVerificationTokenUseCase;
    private final HashEmailVerificationTokenUseCase hashEmailVerificationTokenUseCase;

    private final EmailPort emailPort;
    private final EmailTemplateUseCase emailTemplateUseCase;
    private final TransactionManager transactionManager;

    public SignUpService(
            UserAuthenticationPersistencePort userAuthenticationPort,
            EmailVerificationTokenPort emailVerificationTokenRepositoryPort,
            HashPasswordUseCase hashPasswordUseCase,
            GenerateEmailVerificationTokenUseCase generateEmailVerificationTokenUseCase,
            HashEmailVerificationTokenUseCase hashEmailVerificationTokenUseCase,
            EmailPort emailPort,
            EmailTemplateUseCase emailTemplateUseCase,
            TransactionManager transactionManager
    ) {
        this.userAuthenticationPort = userAuthenticationPort;
        this.emailVerificationTokenRepositoryPort = emailVerificationTokenRepositoryPort;
        this.hashPasswordUseCase = hashPasswordUseCase;
        this.generateEmailVerificationTokenUseCase = generateEmailVerificationTokenUseCase;
        this.hashEmailVerificationTokenUseCase = hashEmailVerificationTokenUseCase;
        this.emailPort = emailPort;
        this.emailTemplateUseCase = emailTemplateUseCase;
        this.transactionManager = transactionManager;
    }

    @Override
    public String execute(String name, String email, String password) {

        UserDomain existingUser = userAuthenticationPort.findByEmail(email).orElse(null);

        if (existingUser != null && existingUser.isVerified()) {
            throw new BusinessException("Email already registered");
        }

        String hashedPassword = hashPasswordUseCase.execute(password);

        boolean isPendingRegistration = existingUser != null;

        UserDomain userDomain = isPendingRegistration
                ? existingUser.updateRegistration(name, hashedPassword)
                : UserDomain.create(name, email, hashedPassword);

        String otpCode = generateEmailVerificationTokenUseCase.execute();
        String otpHash = hashEmailVerificationTokenUseCase.execute(otpCode);

        EmailVerificationTokenDomain token =
                EmailVerificationTokenDomain.create(userDomain.id(), otpHash);

        transactionManager.run(() -> {
            userAuthenticationPort.save(userDomain);

            if (isPendingRegistration) {
                emailVerificationTokenRepositoryPort.deleteByUserId(userDomain.id());
            }

            emailVerificationTokenRepositoryPort.save(token);
        });

        emailPort.send(
                new EmailMessage(
                        userDomain.email(),
                        "Verify your account",
                        emailTemplateUseCase.buildVerificationEmail(otpCode)
                )
        );

        return SUCCESS_MESSAGE;
    }
}
