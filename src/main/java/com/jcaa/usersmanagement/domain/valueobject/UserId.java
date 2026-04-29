package com.jcaa.usersmanagement.domain.valueobject;

import com.jcaa.usersmanagement.domain.exception.InvalidUserIdException;
import java.util.Objects;

// Regla 4 - CORREGIDO: Se usa Objects.requireNonNull en lugar de == null.
public record UserId(String value) {

    public UserId {
        Objects.requireNonNull(value, "UserId cannot be null");
        final String normalizedValue = value.trim();
        validateNotEmpty(normalizedValue);
        value = normalizedValue;
    }

    private static void validateNotEmpty(final String normalizedValue) {
        if (normalizedValue.isEmpty()) {
            throw InvalidUserIdException.becauseValueIsEmpty();
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
