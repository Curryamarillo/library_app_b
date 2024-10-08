package com.gusdev.library_app.repositories;

import com.gusdev.library_app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository <User, Long> {

    boolean existsByEmail(String email);

    User findByEmail(String email);
}
