package com.bookstore_library.book.service;

import com.bookstore_library.book.entity.BookAddedEvent;
import com.google.gson.Gson;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class InventoryAuditService {
    private static final Logger LOGGER = Logger.getLogger(InventoryAuditService.class.getName());
    private final Gson gson;

    public InventoryAuditService() {
        this.gson = new Gson();
    }

    /**
     * Listen for BookAddedEvent and update inventory accordingly.
     *
     * @param message Kafka message containing BookAddedEvent.
     */
    @KafkaListener(topics = "book_events", groupId = "inventory-audit-group")
    public void handleBookAddedEvent(String message) {
        BookAddedEvent event = gson.fromJson(message, BookAddedEvent.class);
        LOGGER.info("Inventory updated for new book: " + event.getTitle());
    }
}
