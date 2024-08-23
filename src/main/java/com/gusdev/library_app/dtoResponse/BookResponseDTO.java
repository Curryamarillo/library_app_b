package com.gusdev.library_app.dtoResponse;

public record BookResponseDTO(Long id,
                              String title,
                              String author,
                              String isbn,
                              Boolean isAvailable) {
}
