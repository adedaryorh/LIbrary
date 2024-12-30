package com.bookstore_library.book.service;

import com.bookstore_library.book.entity.Author;
import com.bookstore_library.book.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {
    @Autowired
    private AuthorRepository authorRepository;

    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }
    public Author getAuthorById(long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplied id is wrong"));
    }

    public Author saveOrUpdateAuthor(Author author) {
        return authorRepository.save(author);
    }
    public void deleteAuthorById(long id) {
        authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplied id does not exit "));
        authorRepository.deleteById(id);
    }
}
