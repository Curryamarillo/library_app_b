package com.gusdev.library_app.dtoRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


public record BookDTO(
        Long id,
        String title,
        String author,
        String isbn,
        Boolean isAvailable) {

}
