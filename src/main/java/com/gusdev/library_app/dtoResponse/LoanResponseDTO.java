package com.gusdev.library_app.dtoResponse;

import java.time.LocalDateTime;


public record LoanResponseDTO(
        Long id,
        Long userId,
        Long bookId,
        LocalDateTime loanDate,
        LocalDateTime returnDate) {

}
