package com.gusdev.library_app.services;

import com.gusdev.library_app.dtoResponse.LoanDTO;
import com.gusdev.library_app.entities.Loan;
import com.gusdev.library_app.exceptions.LoanAlreadyExistsException;
import com.gusdev.library_app.repositories.LoanRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService {


    private final LoanRepository loanRepository;
    private final ModelMapper modelMapper;

    public LoanService(LoanRepository loanRepository, ModelMapper modelMapper) {
        this.loanRepository = loanRepository;
        this.modelMapper = modelMapper;
    }

    public Loan create(Loan loan) {
        Optional<Loan> loanAvailable = loanRepository.findFirstByBookIdAndReturnDateIsNullOrderByLoanDateDesc(loan.getBook().getId());
    if (loanAvailable.isPresent()) {
        throw new LoanAlreadyExistsException("The book is not here");
    } else {
        return loanRepository.save(loan);
    }
    }
    public List<LoanDTO> findAll() {
        Iterable<Loan> loans = loanRepository.findAll();
        List<LoanDTO> loanDTOList = new ArrayList<>();
        for (Loan loan : loans) {
            LoanDTO loanDTO = modelMapper.map(loan, LoanDTO.class);
            loanDTOList.add(loanDTO);
        }
        return loanDTOList;
    }
    public Loan findById(Long id) {
        return loanRepository.findById(id).orElseThrow(() -> new LoanAlreadyExistsException("Book not found"));
    }
    public void deleteById(Long id) {
        loanRepository.deleteById(id);
    }
}
