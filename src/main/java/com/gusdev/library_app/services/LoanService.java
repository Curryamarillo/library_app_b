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
        if(!loan.getBook().getIsAvailable()) {
            throw new LoanAlreadyExistsException("The book is not available for loan");
        }
        loan.getBook().setIsAvailable(false);
        return loanRepository.save(loan);
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
    public LoanDTO findById(Long id) {
        Loan loan = loanRepository.findById(id).orElseThrow(() -> new LoanAlreadyExistsException("Book not found"));
        return modelMapper.map(loan, LoanDTO.class);
    }
    public void deleteById(Long id) {
        loanRepository.deleteById(id);
    }
    public LoanDTO convertToDTO(Loan loan) {
        return modelMapper.map(loan, LoanDTO.class);
    }
}
