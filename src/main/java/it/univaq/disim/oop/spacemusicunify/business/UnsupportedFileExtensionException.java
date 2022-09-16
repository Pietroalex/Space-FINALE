package it.univaq.disim.oop.spacemusicunify.business;

public class UnsupportedFileExtensionException extends BusinessException {

        public UnsupportedFileExtensionException() {
            super();
        }

        public UnsupportedFileExtensionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }

        public UnsupportedFileExtensionException(String message, Throwable cause) {
            super(message, cause);
        }

        public UnsupportedFileExtensionException(String message) {
            super(message);
        }

        public UnsupportedFileExtensionException(Throwable cause) {
            super(cause);
        }
    }

