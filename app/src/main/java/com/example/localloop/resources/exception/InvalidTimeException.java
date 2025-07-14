package com.example.localloop.resources.exception;

public class InvalidTimeException extends Exception {
    public InvalidTimeException() {
        super();
    }
    public InvalidTimeException(String msg) {
        super(msg);
    }
    public InvalidTimeException(String msg, Throwable cause) {
        super(msg,cause);
    }
    public InvalidTimeException(Throwable cause) {
        super(cause);
    }
}
