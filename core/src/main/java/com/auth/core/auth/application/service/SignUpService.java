package com.auth.core.auth.application.service;

import com.auth.core.auth.application.port.in.emailVerification.GenerateEmailVerificationTokenUseCase;
import com.auth.core.auth.application.port.in.emailVerification.HashEmailVerificationTokenUseCase;
import com.auth.core.auth.application.port.in.password.HashPasswordUseCase;
import com.auth.core.auth.application.port.in.sign.SignUpUseCase;
import com.auth.core.auth.application.port.out.EmailVerificationTokenPort;
import com.auth.core.auth.domain.EmailVerificationToken;
import com.auth.core.shared.email.EmailMessage;
import com.auth.core.shared.email.EmailTemplateUseCase;
import com.auth.core.shared.exception.BusinessException;
import com.auth.core.shared.email.EmailPort;
import com.auth.core.shared.transaction.TransactionManager;
import com.auth.core.user.application.port.out.UserAuthenticationPersistencePort;
import com.auth.core.user.application.port.out.UserRepositoryPort;
import com.auth.core.user.domain.User;

public class SignUpService implements SignUpUseCase {

    private static final String SUCCESS_MESSAGE =
            "If the registration can proceed, a verification code has been sent to the provided email.";

    private final UserAuthenticationPersistencePort userAuthenticationPort;
    private final UserRepositoryPort userRepositoryPort;
    private final EmailVerificationTokenPort emailVerificationTokenRepositoryPort;

    private final HashPasswordUseCase hashPasswordUseCase;
    private final GenerateEmailVerificationTokenUseCase generateEmailVerificationTokenUseCase;
    private final HashEmailVerificationTokenUseCase hashEmailVerificationTokenUseCase;

    private final EmailPort emailPort;
    private final EmailTemplateUseCase emailTemplateUseCase;
    private final TransactionManager transactionManager;

    public SignUpService(
            UserAuthenticationPersistencePort userAuthenticationPort,
            UserRepositoryPort userRepositoryPort,
            EmailVerificationTokenPort emailVerificationTokenRepositoryPort,
            HashPasswordUseCase hashPasswordUseCase,
            GenerateEmailVerificationTokenUseCase generateEmailVerificationTokenUseCase,
            HashEmailVerificationTokenUseCase hashEmailVerificationTokenUseCase,
            EmailPort emailPort,
            EmailTemplateUseCase emailTemplateUseCase,
            TransactionManager transactionManager
    ) {
        this.userAuthenticationPort = userAuthenticationPort;
        this.userRepositoryPort = userRepositoryPort;
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

        User user = userAuthenticationPort.findByEmail(email).orElse(null);

        if (user != null) {

            if (user.isVerified()) {
                throw new BusinessException("Email already registered");
            }

            handlePendingRegistration(user, name, password);

            return SUCCESS_MESSAGE;
        }

        String hashedPassword = hashPasswordUseCase.execute(password);

        String otpCode = generateEmailVerificationTokenUseCase.execute();

        String otpHash = hashEmailVerificationTokenUseCase.execute(otpCode);

        User newUser = User.create(
                name,
                email,
                hashedPassword
        );

        transactionManager.execute(() -> {

            userRepositoryPort.save(newUser);

            EmailVerificationToken emailVerificationToken =
                    EmailVerificationToken.create(
                            newUser.id(),
                            otpHash
                    );

            emailVerificationTokenRepositoryPort.save(emailVerificationToken);

            return null;
        });

        emailPort.send(
                new EmailMessage(
                        newUser.email(),
                        "Verify your account",
                        emailTemplateUseCase.buildVerificationEmail(otpCode)
                )
        );

        return SUCCESS_MESSAGE;
    }

    private void handlePendingRegistration(
            User user,
            String name,
            String password
    ) {

        String hashedPassword = hashPasswordUseCase.execute(password);

        String otpCode = generateEmailVerificationTokenUseCase.execute();

        String otpHash = hashEmailVerificationTokenUseCase.execute(otpCode);

        User updatedUser = user.updateRegistration(
                name,
                hashedPassword
        );

        transactionManager.execute(() -> {

            userRepositoryPort.save(updatedUser);

            emailVerificationTokenRepositoryPort.deleteByUserId(updatedUser.id());

            EmailVerificationToken token =
                    EmailVerificationToken.create(
                            updatedUser.id(),
                            otpHash
                    );

            emailVerificationTokenRepositoryPort.save(token);

            return null;
        });

        emailPort.send(
                new EmailMessage(
                        updatedUser.email(),
                        "Verify your account",
                        emailTemplateUseCase.buildVerificationEmail(otpCode)
                )
        );
    }
}