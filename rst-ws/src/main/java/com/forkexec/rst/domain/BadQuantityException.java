package com.forkexec.rst.domain;

public class BadQuantityException extends Exception {

    public BadQuantityException() {
        super();
    }

    public BadQuantityException(String message) {
        super(message);
    }
}
