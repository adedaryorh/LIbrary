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
- Dockerized Kafka, Zookeeper, and PostgreSQL setup

## Requirements
- Java 17 or higher
- Docker (for Kafka, Zookeeper, and PostgreSQL)
- Maven (for building and running the project)

## Running the Application
1. Clone the repository:
   ```bash
   git clone https://github.com/adedaryorh/bookstore-library.git
   cd bookstore-library
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
- **Request Body:**
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

### Register User
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
