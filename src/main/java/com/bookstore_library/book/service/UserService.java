package com.bookstore_library.book.service;

import com.bookstore_library.book.entity.User;
import com.bookstore_library.book.entity.UserRegisteredEvent;
import com.bookstore_library.book.repository.UserRepository;
import com.google.gson.Gson;
import jakarta.transaction.Transactional;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class UserService {
    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());
    private static final String USER_EVENTS_TOPIC = "user_events";

    private final KafkaProducer<String, String> producer;
    private final Gson gson;
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository, KafkaProducer<String, String> producer) {
        this.userRepository = userRepository;
        this.producer = producer;
        this.gson = new Gson();
    }


    public void registerUser(User user) {
        try {
            if (userRepository.existsByUserId(user.getUserId())) {
                throw new RuntimeException("User ID already exists");
            }
            userRepository.save(user);
            LOGGER.info("User persisted successfully: " + user.getUserId());

            // 3. Publish Kafka Event
            UserRegisteredEvent event = new UserRegisteredEvent(user.getUserId(), user.getName());
            String jsonEvent = gson.toJson(event);

            ProducerRecord<String, String> record = new ProducerRecord<>(USER_EVENTS_TOPIC, jsonEvent);
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    LOGGER.log(Level.SEVERE, "Error publishing UserRegisteredEvent", exception);
                    throw new RuntimeException("Kafka event publishing failed");
                } else {
                    LOGGER.info("Published UserRegisteredEvent to topic: " + metadata.topic());
                }
            });

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during user registration", e);
            throw new RuntimeException("User registration failed: " + e.getMessage());
        }
    }


    public User getUserById(String userId) {
        return userRepository.findByUserId(userId).orElse(null);  // Lookup by userId, not id
    }
}
