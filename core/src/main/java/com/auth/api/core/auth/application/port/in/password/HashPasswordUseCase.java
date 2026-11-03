package com.auth.api.core.auth.application.port.in.password;

public interface HashPasswordUseCase {
    String execute(String password);
}
