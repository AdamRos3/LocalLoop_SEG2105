package com.example.localloop.resources.exception;

public class NoSuchEventException extends Exception {
    public NoSuchEventException() {
        super();
    }
    public NoSuchEventException(String msg) {
        super(msg);
    }
    public NoSuchEventException(String msg, Throwable cause) {
        super(msg,cause);
    }
    public NoSuchEventException(Throwable cause) {
        super(cause);
    }
}
