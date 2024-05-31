package com.gusdev.library_app.dtoResponse;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LoanDTO {
    private Long id;
    private Long userId;
    private Long bookId;
    private LocalDateTime loanDate;
    private LocalDateTime returnDate;
}
