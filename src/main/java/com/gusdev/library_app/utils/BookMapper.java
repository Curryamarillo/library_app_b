package com.gusdev.library_app.utils;

import com.gusdev.library_app.dtoRequest.BookRequestDTO;
import com.gusdev.library_app.entities.Book;

public class BookMapper {
    public static BookRequestDTO toDto(Book book) {
        return new BookRequestDTO(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getIsAvailable()
        );
    }
}
