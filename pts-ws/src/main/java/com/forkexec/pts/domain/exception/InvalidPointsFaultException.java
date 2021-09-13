package com.forkexec.pts.domain.exception;

public class InvalidPointsFaultException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidPointsFaultException() {
	}

	public InvalidPointsFaultException(final String message) {
		super(message);
	}

}