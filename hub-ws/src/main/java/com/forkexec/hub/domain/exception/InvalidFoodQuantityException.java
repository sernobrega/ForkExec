package com.forkexec.hub.domain.exception;

public class InvalidFoodQuantityException extends Exception {

    public InvalidFoodQuantityException() {
        super();
    }

    public InvalidFoodQuantityException(String msg) {
        super(msg);
    }
}
