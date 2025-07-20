package com.example.localloop.resources.exception;

public class NoSuchRequestException extends Exception {
    public NoSuchRequestException() {
        super();
    }
    public NoSuchRequestException(String msg) { super(msg); }
    public NoSuchRequestException(String msg, Throwable cause) { super(msg, cause); }
    public NoSuchRequestException(Throwable cause) { super(cause); }

}
