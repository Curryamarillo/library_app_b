package com.gusdev.library_app.repositories;

import com.gusdev.library_app.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByTitle(String name);

    List<Book> findByTitleIgnoreCase(String title);

    List<Book> findByAuthorIgnoreCase(String author);

    List<Book> findByTitleContainingIgnoreCase(String title);
}
