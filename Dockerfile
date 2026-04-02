# Use a fully supported OpenJDK 17 image
FROM eclipse-temurin:17-jdk

# Set working directory inside the container
WORKDIR /app

# Copy all project files to the container
COPY . .

# Build the app using Maven wrapper, skip tests for faster build
RUN ./mvnw clean package -DskipTests

# Expose the default Spring Boot port
EXPOSE 8080

# Run the built jar (update the jar name if your version differs)
CMD ["java", "-jar", "target/Internship_tracker-0.0.1-SNAPSHOT.jar"]
