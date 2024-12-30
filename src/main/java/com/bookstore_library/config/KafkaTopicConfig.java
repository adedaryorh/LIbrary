package com.bookstore_library.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;

public class KafkaTopicConfig {

    @Bean
    public NewTopic bookBorrowedTopic() {
        return new NewTopic("book_borrowed_events", 1, (short) 1);
    }

    @Bean
    public NewTopic bookReturnedTopic() {
        return new NewTopic("book_returned_events", 1, (short) 1);
    }
}
