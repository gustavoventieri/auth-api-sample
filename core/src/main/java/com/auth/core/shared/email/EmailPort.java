package com.auth.core.shared.email;


public interface EmailPort {

    void send(EmailMessage message);

}