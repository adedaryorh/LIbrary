package com.bookstore_library.config;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Define the custom annotation
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MaxBorrowLimitValidator.class)  // Referencing the MaxBorrowLimitValidator class
public @interface MaxBorrowLimit {
    String message() default "A user cannot borrow more than 5 books at a time";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int max();
}
