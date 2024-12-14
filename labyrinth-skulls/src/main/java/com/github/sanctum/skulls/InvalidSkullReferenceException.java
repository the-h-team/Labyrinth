package com.github.sanctum.skulls;

public class InvalidSkullReferenceException extends Exception {
    public InvalidSkullReferenceException(String message) {
        super(message);
    }
    public InvalidSkullReferenceException(Throwable message) {
        super(message);
    }

}
