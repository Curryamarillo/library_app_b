package com.gusdev.library_app.dtoRequest;

import lombok.Data;

@Data
public class BookDTO {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private Boolean isAvailable;
}
