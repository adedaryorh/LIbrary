package com.bookstore_library.book.customs;

import com.bookstore_library.book.entity.Book;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class MaxBorrowLimitValidator implements ConstraintValidator<MaxBorrowLimit, List<Book>> {

    private int maxLimit;

    @Override
    public void initialize(MaxBorrowLimit constraintAnnotation) {
        this.maxLimit = constraintAnnotation.max(); // Initialize with the value from the annotation
    }

    @Override
    public boolean isValid(List<Book> books, ConstraintValidatorContext context) {
        if (books == null) {
            return true;  // Null is considered valid
        }
        return books.size() <= maxLimit; // Validate book list size
    }
}
