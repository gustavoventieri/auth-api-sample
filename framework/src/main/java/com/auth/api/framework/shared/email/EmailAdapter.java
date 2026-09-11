package com.auth.api.framework.shared.email;

import java.nio.charset.StandardCharsets;

import com.auth.core.shared.email.EmailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.auth.core.shared.email.EmailMessage;
import com.auth.core.shared.email.EmailPort;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class EmailAdapter implements EmailPort {

    private final JavaMailSender mailSender;

    public EmailAdapter(
            JavaMailSender mailSender
    ) {
        this.mailSender = mailSender;
    }


    @Override
    public void send(
            EmailMessage message
    ) {

        try {

            MimeMessage mimeMessage =
                    mailSender.createMimeMessage();


            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            mimeMessage,
                            true,
                            StandardCharsets.UTF_8.name()
                    );


            helper.setTo(
                    message.recipient()
            );

            helper.setSubject(
                    message.subject()
            );

            helper.setText(
                    message.html(),
                    true
            );


            mailSender.send(
                    mimeMessage
            );


        } catch (MessagingException exception) {

            throw new EmailSendException(
                    "Failed to send email",
                    exception
            );
        }
    }
}