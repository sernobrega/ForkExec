package com.forkexec.hub.ws;

/**
 *
 * Exception to be thrown when something is wrong with the client.
 *
 */
public class HubServerException extends Exception {

    private static final long serialVersionUID = 1L;

    public HubServerException() {
        super();
    }

    public HubServerException(String message) {
        super(message);
    }

    public HubServerException(Throwable cause) {
        super(cause);
    }

    public HubServerException(String message, Throwable cause) {
        super(message, cause);
    }

}
