# Use Java 21 (Since your project uses Java 21, not 17!)
FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Copy all files
COPY . .

# Give permission to mvnw
RUN chmod +x mvnw

# Build the project (skip tests to speed up deployment)
RUN ./mvnw clean package -DskipTests

# Expose port 8080 (optional but good practice for Render)
EXPOSE 8080

# Run the app (Explicitly point to the built jar file)
CMD ["java", "-jar", "target/demo-0.0.1-SNAPSHOT.jar"]
