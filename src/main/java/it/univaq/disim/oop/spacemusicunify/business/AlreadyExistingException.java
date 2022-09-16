package it.univaq.disim.oop.spacemusicunify.business;

public class AlreadyExistingException extends BusinessException {
	
	public AlreadyExistingException() {
		super();
	}

	public AlreadyExistingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public AlreadyExistingException(String message, Throwable cause) {
		super(message, cause);
	}

	public AlreadyExistingException(String message) {
		super(message);
	}

	public AlreadyExistingException(Throwable cause) {
		super(cause);
	}
}
