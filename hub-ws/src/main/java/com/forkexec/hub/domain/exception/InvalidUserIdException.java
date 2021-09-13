package com.forkexec.hub.domain.exception;

public class InvalidUserIdException extends Exception {

    public InvalidUserIdException() {
        super();
    }

    public InvalidUserIdException(String msg) {
        super(msg);
    }
}
