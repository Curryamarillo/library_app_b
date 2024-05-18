package com.gusdev.library_app.exceptions;

public class UserCantBeDeletedException extends RuntimeException{
    public UserCantBeDeletedException(String message) { super(message); }
}
