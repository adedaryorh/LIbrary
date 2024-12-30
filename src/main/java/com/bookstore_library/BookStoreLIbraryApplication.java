package com.bookstore_library;

import org.springframework.boot.SpringApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableKafka
public class BookStoreLIbraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookStoreLIbraryApplication.class, args);
    }
}
