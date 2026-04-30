package com.jcaa.usersmanagement.domain.exception;

public final class InvalidUserIdException extends DomainException {

    private static final String ID_EMPTY_MESSAGE = "The user id must not be empty.";

    private InvalidUserIdException(final String message) {
        super(message);
    }

    public static InvalidUserIdException becauseValueIsEmpty() {
        return new InvalidUserIdException(ID_EMPTY_MESSAGE);
    }
}