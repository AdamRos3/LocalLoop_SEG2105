package com.example.localloop.resources.exception;

public class NoSuchEventCategoryNameException extends Exception {
    public NoSuchEventCategoryNameException() {
        super();
    }
    public NoSuchEventCategoryNameException(String msg) {
        super(msg);
    }
    public NoSuchEventCategoryNameException(String msg, Throwable cause) {
        super(msg,cause);
    }
    public NoSuchEventCategoryNameException(Throwable cause) {
        super(cause);
    }
}