package com.auth.api.core.shared.email;


public interface EmailPort {

    void send(EmailMessage message);

}