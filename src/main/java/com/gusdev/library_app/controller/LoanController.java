package com.gusdev.library_app.controller;

import com.gusdev.library_app.dtoRequest.LoanRequestDTO;
import com.gusdev.library_app.dtoResponse.LoanResponseDTO;
import com.gusdev.library_app.entities.Loan;
import com.gusdev.library_app.exceptions.LoanNotFoundException;
import com.gusdev.library_app.services.LoanService;
import com.gusdev.library_app.utils.LoanMapper;
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
    public ResponseEntity<List<LoanResponseDTO>> findAll() {
        List<LoanResponseDTO> loans = loanService.findAll();

        List<LoanResponseDTO> loanResponseDTOList = new ArrayList<>(loans);
        return ResponseEntity.ok(loanResponseDTOList);
    }



    @PostMapping("/create")
    public ResponseEntity<LoanResponseDTO> createLoan(@RequestBody LoanRequestDTO loanRequestDTO) {
        Loan createdLoan = loanService.create(loanRequestDTO);
        System.out.println(createdLoan);
        LoanResponseDTO createdLoanResponseDTO = LoanMapper.toDTO(createdLoan);
        System.out.println(createdLoanResponseDTO);
        return new ResponseEntity<>(createdLoanResponseDTO, HttpStatus.CREATED);
    }



    @GetMapping("/{id}")
    public ResponseEntity<LoanResponseDTO> findById(@PathVariable Long id) {
        try {
            LoanResponseDTO loanResponseDTO = loanService.findById(id);
            return ResponseEntity.ok(loanResponseDTO);
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
