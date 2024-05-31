package com.gusdev.library_app.controller;

import com.gusdev.library_app.dtoResponse.LoanDTO;
import com.gusdev.library_app.services.LoanService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.gusdev.library_app.entities.Loan;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;
    private final ModelMapper modelMapper;

    @Autowired
    public LoanController(LoanService loanService, ModelMapper modelMapper) {
        this.loanService = loanService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<List<LoanDTO>> findAll() {
        List<LoanDTO> loans = loanService.findAll();

        List<LoanDTO> loanDTOList = new ArrayList<>();
        for (LoanDTO loan : loans) {
            loanDTOList.add(modelMapper.map(loan, LoanDTO.class));
        }
        return ResponseEntity.ok(loanDTOList);
    }
}
