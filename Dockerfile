FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app
COPY target/health-care-0.0.1-SNAPSHOT.jar app.jar

# Expose port (ví dụ 8000)
EXPOSE 8000

# Biến môi trường tùy chọn
ENV JAVA_OPTS="-Xms1024m -Xmx2048m"

# Command chạy app
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
