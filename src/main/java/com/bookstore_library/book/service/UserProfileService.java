package com.bookstore_library.book.service;

import com.bookstore_library.book.entity.User;
import com.bookstore_library.book.entity.UserRegisteredEvent;
import com.bookstore_library.book.repository.UserRepository;
import com.google.gson.Gson;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class UserProfileService {
    private static final Logger LOGGER = Logger.getLogger(UserProfileService.class.getName());
    private final Gson gson = new Gson();
    private final UserRepository userRepository;

    public UserProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Listen for UserRegisteredEvent from Kafka and create user profiles.
     *
     * @param message Kafka message containing UserRegisteredEvent.
     */
    @KafkaListener(topics = "user_events", groupId = "user-profile-group")
    @Transactional
    public void handleUserRegisteredEvent(String message) {
        try {
            UserRegisteredEvent event = gson.fromJson(message, UserRegisteredEvent.class);
            LOGGER.info("Received UserRegisteredEvent for user: " + event.getName());

            // Check if user already exists by userId (not primary key)
            if (userRepository.findByUserId(event.getUserId()).isPresent()) {
                LOGGER.warning("User with userId " + event.getUserId() + " already exists. Skipping profile creation.");
                return;
            }

            // Create and save new user profile
            User user = new User();
            user.setUserId(event.getUserId());
            user.setName(event.getName());

            userRepository.save(user);
            LOGGER.info("User profile created successfully for: " + event.getName());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing UserRegisteredEvent", e);
        }
    }
}
