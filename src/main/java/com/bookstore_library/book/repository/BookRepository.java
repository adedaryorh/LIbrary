package com.bookstore_library.book.repository;


import com.bookstore_library.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    //Book findByIsbn(String isbn);
    Optional<Book> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);
}
