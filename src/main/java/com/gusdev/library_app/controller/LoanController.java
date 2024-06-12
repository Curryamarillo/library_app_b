package com.gusdev.library_app.controller;

import com.gusdev.library_app.dtoResponse.LoanDTO;
import com.gusdev.library_app.entities.Loan;
import com.gusdev.library_app.exceptions.LoanNotFoundException;
import com.gusdev.library_app.services.LoanService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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



    @PostMapping("/create")
    public ResponseEntity<LoanDTO> createLoan(@RequestBody Loan loan) {
        Loan createdLoan = loanService.create(loan);
        LoanDTO createdLoanDTO = loanService.convertToDTO(createdLoan);
        return new ResponseEntity<>(createdLoanDTO, HttpStatus.CREATED);
    }



    @GetMapping("/{id}")
    public ResponseEntity<LoanDTO> findById(@PathVariable Long id) {
        try {
            LoanDTO loanDTO = loanService.findById(id);
            return ResponseEntity.ok(loanDTO);
        } catch (LoanNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        loanService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
