package com.bookstore_library.config;

import com.bookstore_library.book.entity.Book;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class BorrowLimitValidator implements jakarta.validation.ConstraintValidator<MaxBorrowLimit, List<Book>> {

    private int max;

    @Override
    public void initialize(MaxBorrowLimit constraintAnnotation) {
        this.max = constraintAnnotation.max();
    }
/*
    @Override
    public boolean isValid(List<Book> bookList, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }

 */

    @Override
    public boolean isValid(List<Book> borrowedBooks, ConstraintValidatorContext context) {
        return borrowedBooks.size() <= max;
    }
}