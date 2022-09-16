package it.univaq.disim.oop.spacemusicunify.business;

public class UtenteGenericoNotFoundException extends BusinessException{

	public UtenteGenericoNotFoundException() {
		super();
	}

	public UtenteGenericoNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UtenteGenericoNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public UtenteGenericoNotFoundException(String message) {
		super(message);
	}

	public UtenteGenericoNotFoundException(Throwable cause) {
		super(cause);
	}
	

}
