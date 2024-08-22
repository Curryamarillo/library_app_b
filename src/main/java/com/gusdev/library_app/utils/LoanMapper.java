package com.gusdev.library_app.utils;

import com.gusdev.library_app.dtoResponse.LoanResponseDTO;
import com.gusdev.library_app.entities.Loan;

import java.util.List;
import java.util.stream.Collectors;

public class LoanMapper {
    public static LoanResponseDTO toDTO(Loan loan) {
        return new LoanResponseDTO(
                loan.getId(),
                loan.getUser().getId(),
                loan.getBook().getId(),
                loan.getLoanDate(),
                loan.getReturnDate()
        );
    }
    public static List<LoanResponseDTO> toDTOList(List<Loan> list) {
        return  list.stream()
                .map(LoanMapper::toDTO)
                .collect(Collectors.toList());
    }
}
