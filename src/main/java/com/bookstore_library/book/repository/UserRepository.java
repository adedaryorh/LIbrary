package com.bookstore_library.book.repository;

import com.bookstore_library.book.entity.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @NotNull Optional<User> findById(String userId);
}
