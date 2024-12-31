package com.bookstore_library.book.service;

import com.bookstore_library.book.entity.UserRegisteredEvent;
import com.google.gson.Gson;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class UserNotificationService {
    private static final Logger LOGGER = Logger.getLogger(UserNotificationService.class.getName());
    private final Gson gson = new Gson();

    /**
     * Listens for UserRegisteredEvent and sends notifications.
     *
     * @param message Kafka message containing UserRegisteredEvent.
     */
    @KafkaListener(topics = "user_events", groupId = "user-notification-group")
    public void handleUserRegisteredEvent(String message) {
        UserRegisteredEvent event = gson.fromJson(message, UserRegisteredEvent.class);
        LOGGER.info("Sending notification to user: " + event.getName());
        // Implement notification logic here
    }
}
