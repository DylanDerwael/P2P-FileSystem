package com.sharing.Handlers.Exception;

/**
 * Created by dylan on 6/3/16.
 */
public class CryptoException extends Exception {


    /**
     * Handles the exceptions that can be thrown by the crypto algorithm
     *
     * @param message
     * @param throwable
     */
    public CryptoException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
