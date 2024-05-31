package com.gusdev.library_app.exceptions;

public class LoanAlreadyExistsException extends RuntimeException{
    public LoanAlreadyExistsException(String message) { super(message);}
}
