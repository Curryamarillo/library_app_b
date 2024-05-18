package com.gusdev.library_app.exceptions;

public class BookNotFoundException extends RuntimeException{
    public BookNotFoundException(String message) { super(message);}
}
