package com.bookstore_library.config;

import com.google.gson.Gson;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class EventPublisher {
    private static final Logger LOGGER = Logger.getLogger(EventPublisher.class.getName());
    private final KafkaProducer<String, String> producer;
    private final Gson gson = new Gson();

    @Autowired
    public EventPublisher(KafkaProducer<String, String> producer) {
        this.producer = producer;
    }

    public void publishEvent(String topic, Object event) {
        try {
            String jsonEvent = gson.toJson(event);
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, jsonEvent);
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    LOGGER.log(Level.SEVERE, "Failed to publish event", exception);
                } else {
                    LOGGER.info("Event published to topic: " + metadata.topic());
                }
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during event publishing", e);
        }
    }
}
