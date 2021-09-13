package com.forkexec.rst.domain;

public class BadInitException extends Exception {

    public BadInitException() {
        super();
    }

    public BadInitException(String message) {
        super(message);
    }
}
