# Threat Intelligence Platform

This project is a microservices-based Threat Intelligence Platform built with Spring Boot, Kafka, and MySQL.

## Architecture

*   **Ingestion Service**: Fetches threat intelligence data from external APIs (AbuseIPDB, AlienVault OTX) and publishes to Kafka.
*   **Extraction Service**: Consumes raw data from Kafka, normalizes it into a standardized format (IOC objects), and pushes to another Kafka topic.
*   **Processing Service**: Processes the normalized data, stores it in the MySQL database (`threat_intel`), and exposes REST APIs.
*   **Query Service**: Connects to the same database and exposes read-only endpoints for searching and retrieving IOCs.
*   **Ranking Service**: (Future) Will analyze and rank the severity of threats.

## Setup Instructions

1.  **Database**: Start your MySQL server (via WAMP/XAMPP). Create a database named `threat_intel`. Ensure your username/password match what's in your `application.properties`.
2.  **Kafka**: Start your Zookeeper and Kafka server locally on port 9092.
3.  **Run Services**: Start the services using your IDE (IntelliJ/Eclipse) or `mvn spring-boot:run`.

## Important Note on API Limits

The `IngestionService` fetches data from free tiers of external APIs (AbuseIPDB, AlienVault). To avoid hitting the "429 Too Many Requests" limits, the `fetchFromAbuseIPDB` is scheduled to run once every 24 hours, and `fetchFromAlienVault` every 1 hour. You can use the `/api/ingestion/test-data` endpoint to send mock data to Kafka for testing purposes.
