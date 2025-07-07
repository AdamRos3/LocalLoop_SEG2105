package com.example.localloop.resources.exception;

public class NoSuchEventCategoryException extends Exception {
    public NoSuchEventCategoryException() {
        super();
    }
    public NoSuchEventCategoryException(String msg) {
        super(msg);
    }
    public NoSuchEventCategoryException(String msg, Throwable cause) {
        super(msg,cause);
    }
    public NoSuchEventCategoryException(Throwable cause) {
        super(cause);
    }
}