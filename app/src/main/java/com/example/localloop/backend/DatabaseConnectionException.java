package com.example.localloop.backend;

public class DatabaseConnectionException extends Exception {
    public DatabaseConnectionException() {
        super();
    }
    public DatabaseConnectionException(String message) {
        super(message);
    }
    public DatabaseConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
    public DatabaseConnectionException(Throwable cause) {
        super(cause);
    }
}
