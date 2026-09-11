package com.auth.core.auth.application.port.in.password;

public interface HashPasswordUseCase {
    String execute(String password);
}
