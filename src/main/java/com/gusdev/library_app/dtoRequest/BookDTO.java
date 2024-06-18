package com.gusdev.library_app.dtoRequest;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookDTO {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private Boolean isAvailable;
}
