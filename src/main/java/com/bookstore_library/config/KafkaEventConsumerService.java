package com.bookstore_library.config;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaEventConsumerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaEventConsumerService.class);

    /**
     * Listens to events from Kafka topics (book_events and user_events).
     * Spring manages the consumer lifecycle and ensures thread safety.
     *
     * @param record Kafka message record
     */
    @KafkaListener(topics = {"book_events", "user_events"}, groupId = "event-consumer-group")
    public void listen(ConsumerRecord<String, String> record) {
        LOGGER.info("Received event from topic: {}", record.topic());
        LOGGER.info("Event key: {}", record.key());
        LOGGER.info("Event value: {}", record.value());
        // Add business logic here if needed
    }
}
