package com.auth.api.core.auth.application.port.in.refresh;

public interface CompareRefreshTokenUseCase {
  boolean execute(
            String refreshToken,
            String tokenHash
  );
}
