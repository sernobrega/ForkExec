package com.forkexec.hub.domain.exception;

public class EmptyCartException extends Exception {

    public EmptyCartException() {
        super();
    }

    public EmptyCartException(String msg) {
        super(msg);
    }

}