# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file into the container
COPY target/refundStatusService-0.0.1-SNAPSHOT.jar refund-status-service.jar

# Expose the port the application runs on
EXPOSE 9080

# Run the application
ENTRYPOINT ["java", "-jar", "refund-service.jar"]