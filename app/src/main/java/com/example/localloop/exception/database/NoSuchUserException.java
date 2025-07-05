package com.example.localloop.exception.database;

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
