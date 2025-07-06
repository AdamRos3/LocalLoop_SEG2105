package com.example.localloop.resources.exception;

public class NoSuchUserException extends Exception {
    public NoSuchUserException() {
        super();
    }
    public NoSuchUserException(String msg) {
        super(msg);
    }
    public NoSuchUserException(String msg, Throwable cause) {
        super(msg,cause);
    }
    public NoSuchUserException(Throwable cause) {
        super(cause);
    }
}