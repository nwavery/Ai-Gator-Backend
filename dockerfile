    # Use a base image with Java 17
    FROM eclipse-temurin:17-jdk-jammy
    
    # Set the working directory inside the container
    WORKDIR /app
    
    # Argument to hold the path to the JAR file
    ARG JAR_FILE=target/*.jar
    
    # Copy the JAR file into the container
    COPY ${JAR_FILE} app.jar
    
    # Expose the port the application runs on
    EXPOSE 8080
    
    # Command to run the application
    ENTRYPOINT ["java", "-jar", "/app/app.jar"]