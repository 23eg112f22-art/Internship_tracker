# Use official Java runtime
FROM openjdk:17-slim

# Set working directory
WORKDIR /app

# Copy all files to container
COPY . .

# Build the app using Maven
RUN ./mvnw clean package -DskipTests

# Expose Spring Boot default port
EXPOSE 8080

# Run the jar
CMD ["java", "-jar", "target/internship-tracker-0.0.1-SNAPSHOT.jar"]
