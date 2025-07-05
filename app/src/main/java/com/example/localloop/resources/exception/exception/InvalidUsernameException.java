package com.example.localloop.resources.exception.exception;

public class InvalidUsernameException extends Exception {
    public InvalidUsernameException() {
        super();
    }
    public InvalidUsernameException(String msg) {
        super(msg);
    }
    public InvalidUsernameException(String msg, Throwable cause) {
        super(msg,cause);
    }
    public InvalidUsernameException(Throwable cause) {
        super(cause);
    }
}