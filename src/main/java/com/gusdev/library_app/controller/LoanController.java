package com.gusdev.library_app.controller;

import com.gusdev.library_app.dtoResponse.LoanDTO;
import com.gusdev.library_app.entities.Loan;
import com.gusdev.library_app.exceptions.LoanNotFoundException;
import com.gusdev.library_app.services.LoanService;
import com.gusdev.library_app.utils.LoanMapper;
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


    @Autowired
    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }



    @GetMapping
    public ResponseEntity<List<LoanDTO>> findAll() {
        List<LoanDTO> loans = loanService.findAll();

        List<LoanDTO> loanDTOList = new ArrayList<>();
        for (LoanDTO loan : loans) {
            loanDTOList.add(loan);
        }
        return ResponseEntity.ok(loanDTOList);
    }



    @PostMapping("/create")
    public ResponseEntity<LoanDTO> createLoan(@RequestBody Loan loan) {
        Loan createdLoan = loanService.create(loan);
        LoanDTO createdLoanDTO = LoanMapper.toDTO(createdLoan);
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
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        loanService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
