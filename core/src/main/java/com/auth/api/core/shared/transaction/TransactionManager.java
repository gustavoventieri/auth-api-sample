package com.auth.api.core.shared.transaction;

import java.util.function.Supplier;

public interface TransactionManager {
    <T> T execute(Supplier<T> action);

    default void run(Runnable action) {
        execute(() -> {
            action.run();
            return null;
        });
    }
}
