package com.gusdev.library_app.exceptions;

public class InvalidPasswordException extends RuntimeException{
    public InvalidPasswordException(String message) { super(message); }
}
