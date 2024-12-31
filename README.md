# Bookstore Library Management System

## Overview
This is a Java-based Library Management System that supports adding, borrowing, and returning books. It uses Spring Boot, Kafka, Hibernate, and PostgreSQL for event-driven design, persistence, and asynchronous communication.


## Features
- Book and User management
- Custom annotations for ISBN validation and borrow limits
- Kafka-based event publishing and consumption
- Exception handling and validation
- PostgreSQL database with Docker support
- Embedded Kafka for integration testing
- Dockerized Kafka, Zookeeper, Aminner and PostgreSQL setup

## Requirements
- Java 17 or higher
- Docker (for Kafka, Zookeeper, adminner and PostgreSQL)
- Maven (for building and running the project)


##PostMan API Documentation Link
https://documenter.getpostman.com/view/17470462/2sAYJ7fyzy

## Running the Application
1. Clone the repository:
   ```bash
   git clone https://github.com/adedaryorh/LIbrary_BookStore.git
   cd LIbrary_BookStore
   ```
2. Start Kafka, Zookeeper, and PostgreSQL using Docker Compose:
   ```bash
   docker-compose up -d
   ```
3. Build and run the application:
   ```bash
   mvn spring-boot:run
   ```
4. Verify PostgreSQL connection:
   ```bash
   psql -U library_user -d librarydb -h localhost
   ```
   Use password `library_password` when prompted.

5. Verify Kafka UI (Optional):
   ```
   http://localhost:8080
   ```

## Testing
Run the test suite with:
```bash
mvn test
```

### Running Kafka-Dependent Tests
Ensure Kafka and PostgreSQL are up and running before executing tests.
```bash
docker-compose up -d
mvn test
```

## API Endpoints

### Add Book
- **Endpoint:** `POST /library/addBook`
- ** Sample Request Body:**
```json
{
  "title": "Effective Java",
  "author": "Joshua Bloch",
  "isbn": "9780134685991"
}
```
- **Response:**
```json
{
  "message": "Book added successfully!",
  "isbn": "9780134685991"
}
```

### Register A User
- **Endpoint:** `POST /library/register`
- **Request Body:**
```json
{
  "name": "Alice",
  "userId": "user001"
}
```
- **Response:**
```json
{
  "message": "User registered successfully!",
  "userId": "user001"
}
```

### Borrow Book
- **Endpoint:** `POST /library/borrow`
- **Request Body:**
```json
{
  "userId": "user001",
  "isbn": "9780134685991"
}
```
- **Response:**
```json
{
  "message": "Book borrowed successfully!"
}
```

### Return Book
- **Endpoint:** `POST /library/return`
- **Request Body:**
```json
{
  "userId": "user001",
  "isbn": "9780134685991"
}
```
- **Response:**
```json
{
  "message": "Book returned successfully!"
}
```

## Event Topics
- `book-added`
- `book-borrowed`

## Notes
- Ensure Kafka and PostgreSQL are running before borrowing or returning books, as these trigger Kafka events.
- Kafka UI can be accessed at `http://localhost:8080` for monitoring topics.
- For troubleshooting, check Docker logs:
   ```bash
   docker-compose logs kafka
   ```
- Embedded Kafka is used in the test environment for seamless integration testing without external dependencies.




## Tessting Even driven via CLI 
These Even works automatication when any of the events are triggered via the postman API and also below payloads can be done on the CLI. 
Setup Kafka Topics (Ensure Topics Exist)

-  kafka-topics.sh --create --topic user_events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
-  kafka-topics.sh --create --topic book_events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
-  kafka-topics.sh --create --topic book_borrowed_events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
-  kafka-topics.sh --create --topic book_returned_events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

## Verify the topics
-  kafka-topics.sh --list --bootstrap-server localhost:9092

## Producer: Simulate Adding Users and Books

kafka-console-producer.sh --bootstrap-server localhost:9092 --topic user_events

## Sample payloads 
>>{"userId":"user1118","name":"JUJU toha"}

## Add Books (Produce to book_events)
kafka-console-producer.sh --bootstrap-server localhost:9092 --topic book_events

>>{"isbn":"9780060935467","title":"Essentials of Java","author":"James Clear"}


## Consumer: Verify User and Book Events Creation
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic user_events --from-beginning

>>
{"userId":"user8978","name":"John Doe"}
{"userId":"user8979","name":"Jane Smith"}

## Consume Book Events
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic book_events --from-beginning

>>{"isbn":"9780060935467","title":"Essentials of Java","author":"James Clear"}

## Simulate Borrowing and Returning Books

kafka-console-producer.sh --bootstrap-server localhost:9092 --topic book_borrowed_events

>>{"isbn":"9780399590504","userId":"user1118"}
>>{"isbn":"9780132350884","userId":"user8979"}

## Return Books (Produce to book_returned_events)

kafka-console-producer.sh --bootstrap-server localhost:9092 --topic book_returned_events

>>{"isbn":"9780060935467","userId":"user8978"}

## Consume Borrowed Events
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic book_borrowed_events --from-beginning

>>{"isbn":"9780060935467","userId":"user8978"}

## Consume Returned Events
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic book_returned_events --from-beginning

>>{"isbn":"9780060935467","userId":"user8978"}

