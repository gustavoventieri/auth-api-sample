package com.auth.api.framework.auth.utils.password;



import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptProvider {

    private final BCryptPasswordEncoder encoder;

    public BCryptProvider(BCryptProperties properties) {
        this.encoder = new BCryptPasswordEncoder(properties.strength());
    }
    public String encode(String password) {
        return encoder.encode(password);
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}