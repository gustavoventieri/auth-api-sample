package com.auth.core.auth.application.port.in.sign;

public interface SignUpUseCase {
    String execute(String name, String email, String password);
}
