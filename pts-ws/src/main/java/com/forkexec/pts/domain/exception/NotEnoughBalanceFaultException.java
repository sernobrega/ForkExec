package com.forkexec.pts.domain.exception;

public class NotEnoughBalanceFaultException extends Exception {

	private static final long serialVersionUID = 1L;

	public NotEnoughBalanceFaultException() {
	}

	public NotEnoughBalanceFaultException(final String message) {
		super(message);
	}

}