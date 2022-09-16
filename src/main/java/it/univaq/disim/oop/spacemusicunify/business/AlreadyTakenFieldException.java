package it.univaq.disim.oop.spacemusicunify.business;

public class AlreadyTakenFieldException extends BusinessException {

    public AlreadyTakenFieldException() {
        super();
    }

    public AlreadyTakenFieldException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public AlreadyTakenFieldException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyTakenFieldException(String message) {
        super(message);
    }

    public AlreadyTakenFieldException(Throwable cause) {
        super(cause);
    }
}
