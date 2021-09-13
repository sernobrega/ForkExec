package com.forkexec.rst.domain;

public class BadTextException extends Exception {

    public BadTextException() {
        super();
    }

    public BadTextException(String message) {
        super(message);
    }
}
