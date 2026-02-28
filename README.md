# Fraud Detection API Server

## Project Overview
This project is a backend API server built with Spring Boot for a Fraud Detection Service. It acts as a central control tower, handling client requests, permanently storing evidence images in cloud storage (Supabase), and communicating with a Python-based AI model server to verify whether the images are fraudulent.

## Key Architecture & Technical Decisions
- **Two-Track Image Processing:** To minimize network latency, the Spring Boot server simultaneously uploads the original image to Supabase Storage (for permanent retention) and directly transfers the multipart file to the Python AI Server (for real-time analysis). This optimizes the overall system response time.
- **Domain-Driven Storage Architecture:** Instead of traditional date-based folders (`yyyy/mm/dd`), images are grouped into unique `UUID` folders per verification session. This ensures absolute uniqueness, prevents filename collisions, and provides high data cohesion for multi-image verification workflows.
- **Data Persistence:** The final detection results (Fraud/Normal/Suspicious) and the generated cloud storage URLs are safely recorded in a PostgreSQL database using Spring Data JPA.

## Tech Stack
- **Framework:** Java, Spring Boot
- **Database / Storage:** Supabase (PostgreSQL), Supabase Storage
- **ORM:** Spring Data JPA, Hibernate
- **Tooling:** Lombok, Gradle

## System Flow
1. Client uploads evidence images (e.g., chat screenshots) to the Spring Boot endpoint.
2. Spring Boot generates a unique verification session UUID.
3. Uploads the image to Supabase Storage and retrieves the Public URL.
4. Simultaneously forwards the multipart image directly to the Python AI Server for classification.
5. Saves the final result (Fraud/Suspicious/Normal) from the AI server and the Storage URL into the Database (PostgreSQL).
6. Returns the final result to the Client.