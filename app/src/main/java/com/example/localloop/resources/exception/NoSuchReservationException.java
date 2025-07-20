package com.example.localloop.resources.exception;

public class NoSuchReservationException extends Exception {
    public NoSuchReservationException() {
        super();
    }
    public NoSuchReservationException(String msg) {
        super(msg);
    }
    public NoSuchReservationException(String msg, Throwable cause) {
        super(msg,cause);
    }
    public NoSuchReservationException(Throwable cause) {
        super(cause);
    }
}
