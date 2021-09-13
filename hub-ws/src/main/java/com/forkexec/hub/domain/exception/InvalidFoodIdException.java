package com.forkexec.hub.domain.exception;

public class InvalidFoodIdException extends Exception {

    public InvalidFoodIdException() {
        super();
    }

    public InvalidFoodIdException(String msg) {
        super(msg);
    }
}
