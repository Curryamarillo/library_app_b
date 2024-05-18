package com.gusdev.library_app.repositories;

import com.gusdev.library_app.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByTitle(String name);

    Iterable<Book> findByTitleIgnoreCase(String title);

    Iterable<Book> findByAuthorIgnoreCase(String author);
}
