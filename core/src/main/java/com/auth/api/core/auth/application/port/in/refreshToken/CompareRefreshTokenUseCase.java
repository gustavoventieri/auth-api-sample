package com.auth.api.core.auth.application.port.in.refreshToken;

public interface CompareRefreshTokenUseCase {
  boolean execute(
            String refreshToken,
            String tokenHash
  );
}
