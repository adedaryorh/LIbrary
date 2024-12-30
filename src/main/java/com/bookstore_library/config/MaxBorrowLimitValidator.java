package com.bookstore_library.config;

import com.bookstore_library.book.entity.User;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MaxBorrowLimitValidator implements ConstraintValidator<MaxBorrowLimit, User> {

    @Override
    public void initialize(MaxBorrowLimit constraintAnnotation) {
        // Initialization code, if needed
    }

    @Override
    public boolean isValid(User user, ConstraintValidatorContext context) {
        if (user == null) {
            return true; // If the user is null, it is considered valid
        }
        // Check if the user has borrowed 5 or more books
        return user.getBorrowedBooks().size() <= 5;
    }
}