package com.gusdev.library_app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private  String title;

    @Column(nullable = false)
    private  String author;

    @Column(length = 100)
    private String isbn;

    @Column(nullable = false)
    private Boolean isAvailable = true;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @OneToMany(mappedBy = "book", fetch = FetchType.EAGER)
    private Set<Loan> loans = new HashSet<>();

}
