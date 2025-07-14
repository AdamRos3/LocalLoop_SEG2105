package com.example.localloop.resources.exception;

public class InvalidDateException extends Exception {
    public InvalidDateException() {
        super();
    }
    public InvalidDateException(String msg) {
        super(msg);
    }
    public InvalidDateException(String msg, Throwable cause) {
        super(msg,cause);
    }
    public InvalidDateException(Throwable cause) {
        super(cause);
    }
}
