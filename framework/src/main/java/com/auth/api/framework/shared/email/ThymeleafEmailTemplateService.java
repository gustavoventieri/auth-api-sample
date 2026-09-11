package com.auth.api.framework.shared.email;

import com.auth.core.shared.email.EmailTemplateUseCase;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.format.DateTimeFormatter;

@Service
public class ThymeleafEmailTemplateService
        implements EmailTemplateUseCase {


    private final TemplateEngine templateEngine;

    public ThymeleafEmailTemplateService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }



    @Override
    public String buildVerificationEmail(String otpCode) {

        Context context = new Context();

        context.setVariable(
                "otpCode",
                otpCode
        );



        return templateEngine.process(
                "verification-email",
                context
        );
    }

    @Override
    public String buildAccountCreatedEmail(
            String name,
            String otpCode
    ) {

        Context context = new Context();

        context.setVariable(
                "name",
                name
        );

        context.setVariable(
                "otpCode",
                otpCode
        );



        return templateEngine.process(
                "account-created-email",
                context
        );
    }

    @Override
    public String buildNewLoginEmail(
            String name,
            String device,
            String ip,
            String dateTime
    ) {

        Context context = new Context();

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

        context.setVariable(
                "name",
                name
        );

        context.setVariable(
                "device",
                device
        );

        context.setVariable(
                "ip",
                ip
        );

        context.setVariable(
                "dateTime",
                dateTime
        );



        return templateEngine.process(
                "new-login-email",
                context
        );
    }


}