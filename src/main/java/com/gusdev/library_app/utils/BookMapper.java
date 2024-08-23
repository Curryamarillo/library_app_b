package com.gusdev.library_app.utils;

import com.gusdev.library_app.dtoRequest.BookRequestDTO;
import com.gusdev.library_app.dtoResponse.BookResponseDTO;
import com.gusdev.library_app.entities.Book;

public class BookMapper {
    public static BookResponseDTO toDto(Book book) {
        return new BookResponseDTO(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getIsAvailable()
        );
    }
}
