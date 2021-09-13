package com.forkexec.rst.domain;

public class InsufficientQuantityException extends Exception {

    public InsufficientQuantityException() {
        super();
    }

    public InsufficientQuantityException(String message) {
        super(message);
    }
}
