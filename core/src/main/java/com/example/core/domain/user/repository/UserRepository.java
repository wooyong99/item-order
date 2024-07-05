package com.example.core.domain.user.repository;

import com.example.core.domain.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>, UserCustomRepository {

    Optional<User> findById(Long userId);
    Optional<User> findByEmail(String username);

    boolean existsByEmail(String email);

}
