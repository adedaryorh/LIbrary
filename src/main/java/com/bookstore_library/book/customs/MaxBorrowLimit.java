package com.bookstore_library.book.customs;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MaxBorrowLimitValidator.class)  // Link to the validator
public @interface MaxBorrowLimit {
    String message() default "Borrowing limit exceeded";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    int max();  // Maximum allowed books
}
