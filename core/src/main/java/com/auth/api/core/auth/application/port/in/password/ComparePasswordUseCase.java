package com.auth.api.core.auth.application.port.in.password;

public interface ComparePasswordUseCase {
    boolean execute(String password, String hashPassword);
}
