package com.example.localloop.resources.exception;

public class InvalidEventCategoryNameException extends Exception {
    public InvalidEventCategoryNameException() {
        super();
    }
    public InvalidEventCategoryNameException(String msg) {
        super(msg);
    }
    public InvalidEventCategoryNameException(String msg, Throwable cause) {
        super(msg,cause);
    }
    public InvalidEventCategoryNameException(Throwable cause) {
        super(cause);
    }
}
