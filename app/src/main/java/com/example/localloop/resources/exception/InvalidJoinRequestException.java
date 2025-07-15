package com.example.localloop.resources.exception;

public class InvalidJoinRequestException extends Exception {
    public InvalidJoinRequestException() {
        super();
    }
    public InvalidJoinRequestException(String msg) {
        super(msg);
    }
    public InvalidJoinRequestException(String msg, Throwable cause) {
        super(msg,cause);
    }
    public InvalidJoinRequestException(Throwable cause) {
        super(cause);
    }
}
