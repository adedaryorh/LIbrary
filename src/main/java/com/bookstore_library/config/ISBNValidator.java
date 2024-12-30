package com.bookstore_library.config;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ISBNValidator implements ConstraintValidator<ValidateISBN, String> {

    @Override
    public void initialize(ValidateISBN constraintAnnotation) {
        // Initialization logic if required. Otherwise, you can leave it empty.
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Null or empty ISBN is considered invalid
        if (value == null || value.isEmpty()) {
            return false;
        }

        // Validate ISBN-10
        if (value.matches("\\d{9}[\\dX]")) {
            return isValidISBN10(value);
        }

        // Validate ISBN-13
        if (value.matches("\\d{13}")) {
            return isValidISBN13(value);
        }

        // If not matching ISBN-10 or ISBN-13 format, it is invalid
        return false;
    }

    private boolean isValidISBN10(String isbn) {
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (isbn.charAt(i) - '0') * (10 - i);
        }
        char check = isbn.charAt(9);
        sum += (check == 'X' ? 10 : (check - '0'));
        return sum % 11 == 0;
    }

    private boolean isValidISBN13(String isbn) {
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = isbn.charAt(i) - '0';
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        int checkDigit = 10 - (sum % 10);
        if (checkDigit == 10) {
            checkDigit = 0;
        }
        return checkDigit == (isbn.charAt(12) - '0');
    }
}
