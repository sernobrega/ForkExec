package com.forkexec.pts.domain.exception;

public class EmailAlreadyExistsFaultException extends Exception {

	private static final long serialVersionUID = 1L;

	public EmailAlreadyExistsFaultException() {
	}

	public EmailAlreadyExistsFaultException(final String message) {
		super(message);
	}

}