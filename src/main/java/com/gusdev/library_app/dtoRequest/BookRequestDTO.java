package com.gusdev.library_app.dtoRequest;


public record BookRequestDTO(
        Long id,
        String title,
        String author,
        String isbn,
        Boolean isAvailable) {

}
