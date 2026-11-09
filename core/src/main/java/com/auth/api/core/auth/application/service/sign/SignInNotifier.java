package com.auth.api.core.auth.application.service.sign;

import com.auth.api.core.shared.email.EmailMessage;
import com.auth.api.core.shared.email.EmailPort;
import com.auth.api.core.shared.email.EmailTemplateUseCase;
import com.auth.api.core.user.domain.UserDomain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Envia o e-mail de "novo acesso". Disparado apenas quando o login é concluído
 * (após o segundo fator), não mais após a senha.
 */
public class SignInNotifier {

    private static final DateTimeFormatter LOGIN_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    private final EmailPort emailPort;
    private final EmailTemplateUseCase emailTemplateUseCase;

    public SignInNotifier(EmailPort emailPort, EmailTemplateUseCase emailTemplateUseCase) {
        this.emailPort = emailPort;
        this.emailTemplateUseCase = emailTemplateUseCase;
    }

    public void notifyNewSignIn(UserDomain user, String ip, String device) {
        emailPort.send(
                new EmailMessage(
                        user.email(),
                        "New sign-in to your account",
                        emailTemplateUseCase.buildNewLoginEmail(
                                user.name(),
                                device,
                                ip,
                                LocalDateTime.now().format(LOGIN_DATE_FORMAT)
                        )
                )
        );
    }
}
