package com.bookstore_library.exceptions;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDetail {
    private LocalDateTime timestamp;
    private String message;
    private String details;

    public ErrorDetail(LocalDateTime timestamp, String message, String details) {
        super();
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
    }

    public ErrorDetail(String message, String details) {
        this.message = message;
        this.details = details;
    }
}
