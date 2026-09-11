package com.auth.core.shared.transaction;

import java.util.function.Supplier;

public interface TransactionManager {
    <T> T execute(Supplier<T> action);
}