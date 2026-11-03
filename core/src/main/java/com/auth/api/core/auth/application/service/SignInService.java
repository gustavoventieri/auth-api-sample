package com.auth.api.core.auth.application.service;

import com.auth.api.core.auth.application.dto.response.AuthResponse;
import com.auth.api.core.auth.application.port.in.password.ComparePasswordUseCase;
import com.auth.api.core.auth.application.port.in.sign.IssueAuthTokensUseCase;
import com.auth.api.core.auth.application.port.in.sign.SignInUseCase;
import com.auth.api.core.shared.email.EmailMessage;
import com.auth.api.core.shared.email.EmailPort;
import com.auth.api.core.shared.email.EmailTemplateUseCase;
import com.auth.api.core.shared.exception.BusinessException;
import com.auth.api.core.shared.exception.NotFoundException;
import com.auth.api.core.user.application.port.out.UserAuthenticationPersistencePort;
import com.auth.api.core.user.domain.UserDomain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SignInService implements SignInUseCase {

    private static final String INVALID_CREDENTIALS = "Email or Password Invalid";
    private static final DateTimeFormatter LOGIN_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    private final UserAuthenticationPersistencePort userAuthenticationPort;
    private final ComparePasswordUseCase comparePasswordUseCase;
    private final IssueAuthTokensUseCase issueAuthTokensUseCase;
    private final EmailPort emailPort;
    private final EmailTemplateUseCase emailTemplateUseCase;

    public SignInService(
            UserAuthenticationPersistencePort userAuthenticationPort,
            ComparePasswordUseCase comparePasswordUseCase,
            IssueAuthTokensUseCase issueAuthTokensUseCase,
            EmailPort emailPort,
            EmailTemplateUseCase emailTemplateUseCase
    ) {
        this.userAuthenticationPort = userAuthenticationPort;
        this.comparePasswordUseCase = comparePasswordUseCase;
        this.issueAuthTokensUseCase = issueAuthTokensUseCase;
        this.emailPort = emailPort;
        this.emailTemplateUseCase = emailTemplateUseCase;
    }

    @Override
    public AuthResponse execute(String email, String password, String ip, String device) {

        UserDomain userDomain = userAuthenticationPort.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(INVALID_CREDENTIALS));

        if (!userDomain.isVerified()) throw new BusinessException("Email not verified yet");

        if (!userDomain.isActive()) throw new BusinessException("User not Active");

        if (!comparePasswordUseCase.execute(password, userDomain.password())) {
            throw new NotFoundException(INVALID_CREDENTIALS);
        }

        AuthResponse response = issueAuthTokensUseCase.execute(userDomain, ip, device);

        emailPort.send(
                new EmailMessage(
                        userDomain.email(),
                        "New sign-in to your account",
                        emailTemplateUseCase.buildNewLoginEmail(
                                userDomain.name(),
                                device,
                                ip,
                                LocalDateTime.now().format(LOGIN_DATE_FORMAT)
                        )
                )
        );

        return response;
    }
}
