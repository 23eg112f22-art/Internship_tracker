# Use a fully qualified OpenJDK 17 image
FROM eclipse-temurin:17-jdk

# Set working directory
WORKDIR /app

# Copy all project files
COPY . .

# Build the app using Maven wrapper
RUN ./mvnw clean package -DskipTests

# Expose Spring Boot default port
EXPOSE 8080

# Run the built jar (update name if needed)
CMD ["java", "-jar", "target/internship-tracker-0.0.1-SNAPSHOT.jar"]
