package com.example.localloop.resources.exception;

public class InvalidEventNameException extends Exception {
    public InvalidEventNameException() {
        super();
    }
    public InvalidEventNameException(String msg) {
        super(msg);
    }
    public InvalidEventNameException(String msg, Throwable cause) {
        super(msg,cause);
    }
    public InvalidEventNameException(Throwable cause) {
        super(cause);
    }
}
