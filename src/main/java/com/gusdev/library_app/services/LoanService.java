package com.gusdev.library_app.services;

import com.gusdev.library_app.dtoResponse.LoanDTO;
import com.gusdev.library_app.entities.Loan;
import com.gusdev.library_app.exceptions.LoanAlreadyExistsException;
import com.gusdev.library_app.exceptions.LoanNotFoundException;
import com.gusdev.library_app.repositories.LoanRepository;
import com.gusdev.library_app.utils.LoanMapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class LoanService {


    private final LoanRepository loanRepository;

    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    public Loan create(Loan loan) {
        if(!loan.getBook().getIsAvailable()) {
            throw new LoanAlreadyExistsException("The book is not available for loan");
        }
        loan.getBook().setIsAvailable(false);
        return loanRepository.save(loan);
    }
    public List<LoanDTO> findAll() {
        List<Loan> loans = loanRepository.findAll();
        return LoanMapper.toDTOList(loans);
    }
    public LoanDTO findById(Long id) {
        Loan loan = loanRepository.findById(id).orElseThrow(() -> new LoanNotFoundException("Book not found"));
        return LoanMapper.toDTO(loan);
    }
    public void deleteById(Long id) {
        loanRepository.deleteById(id);
    }

}
