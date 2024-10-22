package com.gusdev.library_app.repositories;

import com.gusdev.library_app.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByTitle(String name);

    List<Book> findByTitleIgnoreCase(String title);

    List<Book> findByAuthorIgnoreCase(String author);

    List<Book> findByTitleContainingIgnoreCase(String title);

    @Query("SELECT b.title FROM Loan l JOIN Book b ON l.book.id = b.id WHERE l.user.id = :userId AND l.returnDate IS NULL")
    List<String> findBookTitlesByUserId(@Param("userId") Long userId);

}
