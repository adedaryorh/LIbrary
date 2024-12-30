package com.bookstore_library.book.service;

import com.bookstore_library.book.entity.User;
import com.bookstore_library.book.entity.UserRegisteredEvent;
import com.bookstore_library.book.repository.UserRepository;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import com.google.gson.Gson;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Properties;

//UserRegisterEvent
//Publisher (Producer): UserService

@Service
public class UserService {

    private final Gson gson = new Gson();
    private final UserRepository userRepository;
    private KafkaProducer<String, String> producer;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private KafkaProducer<String, String> getProducer() {
        if (producer == null) {
            producer = new KafkaProducer<>(getProducerProps());
        }
        return producer;
    }

    private Properties getProducerProps() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return props;
    }

    public void registerUser(User user) {
        UserRegisteredEvent event = new UserRegisteredEvent(user.getUserId(), user.getName());
        String jsonEvent = gson.toJson(event);

        // Lazily initialize producer
        ProducerRecord<String, String> record = new ProducerRecord<>("user_events", jsonEvent);
        getProducer().send(record, (metadata, exception) -> {
            if (exception != null) {
                System.out.println("Error publishing UserRegisteredEvent: " + exception.getMessage());
            } else {
                System.out.println("Published UserRegisteredEvent to user_events topic");
            }
        });
    }

    public User getUserById(String userId) {
        return userRepository.findById(userId).orElse(null);
    }
}
