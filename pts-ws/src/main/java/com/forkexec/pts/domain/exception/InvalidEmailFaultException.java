package com.forkexec.pts.domain.exception;

public class InvalidEmailFaultException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidEmailFaultException() {
	}

	public InvalidEmailFaultException(final String message) {
		super(message);
	}

}