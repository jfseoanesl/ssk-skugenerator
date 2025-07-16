# ===================================================================
# SKU Generator Application - Dockerfile
# ===================================================================
# Multi-stage build for optimal image size and security
# ===================================================================

# ===================================================================
# Stage 1: Build Stage
# ===================================================================
FROM openjdk:17-jdk-slim AS builder

# Set build arguments
ARG JAR_FILE=target/sku-generator-*.jar
ARG BUILD_DATE
ARG VERSION
ARG GIT_COMMIT

# Set metadata labels
LABEL org.opencontainers.image.title="SKU Generator - Build Stage"
LABEL org.opencontainers.image.description="Build stage for SKU Generator application"
LABEL org.opencontainers.image.version=${VERSION}
LABEL org.opencontainers.image.created=${BUILD_DATE}
LABEL org.opencontainers.image.revision=${GIT_COMMIT}

# Install build dependencies
RUN apt-get update && apt-get install -y \
    wget \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Create application directory
WORKDIR /build

# Copy and extract the JAR file for layered approach
COPY ${JAR_FILE} application.jar

# Extract JAR layers for better Docker layer caching
RUN java -Djarmode=layertools -jar application.jar extract

# ===================================================================
# Stage 2: Runtime Stage
# ===================================================================
FROM openjdk:17-jre-slim AS runtime

# Set build arguments for runtime
ARG BUILD_DATE
ARG VERSION
ARG GIT_COMMIT

# Set metadata labels for runtime image
LABEL org.opencontainers.image.title="SKU Generator Application"
LABEL org.opencontainers.image.description="Sistema de Generación de Códigos SKU para Tienda de Ropa Infantil"
LABEL org.opencontainers.image.version=${VERSION}
LABEL org.opencontainers.image.created=${BUILD_DATE}
LABEL org.opencontainers.image.revision=${GIT_COMMIT}
LABEL org.opencontainers.image.vendor="SKU Generator Development Team"
LABEL org.opencontainers.image.authors="dev@empresa.com"
LABEL org.opencontainers.image.url="https://github.com/empresa/sku-generator"
LABEL org.opencontainers.image.documentation="https://github.com/empresa/sku-generator/blob/main/README.md"
LABEL org.opencontainers.image.source="https://github.com/empresa/sku-generator"
LABEL org.opencontainers.image.licenses="MIT"

# Create non-root user for security
RUN groupadd -r skuapp && useradd -r -g skuapp skuapp

# Install runtime dependencies
RUN apt-get update && apt-get install -y \
    wget \
    curl \
    tzdata \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/* \
    && apt-get clean

# Set timezone
ENV TZ=America/Bogota
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Create application directories
RUN mkdir -p /app/config \
             /app/logs \
             /app/storage/backups \
             /app/storage/exports \
             /app/storage/temp \
             /app/storage/uploads \
    && chown -R skuapp:skuapp /app

# Set working directory
WORKDIR /app

# Copy application layers from builder stage for better caching
COPY --from=builder --chown=skuapp:skuapp /build/dependencies/ ./
COPY --from=builder --chown=skuapp:skuapp /build/spring-boot-loader/ ./
COPY --from=builder --chown=skuapp:skuapp /build/snapshot-dependencies/ ./
COPY --from=builder --chown=skuapp:skuapp /build/application/ ./

# Copy additional configuration files
COPY --chown=skuapp:skuapp docker/app/entrypoint.sh /app/entrypoint.sh
COPY --chown=skuapp:skuapp docker/app/healthcheck.sh /app/healthcheck.sh

# Make scripts executable
RUN chmod +x /app/entrypoint.sh /app/healthcheck.sh

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseStringDeduplication"
ENV SERVER_PORT=8080
ENV LOG_FILE=/app/logs/sku-generator.log
ENV STORAGE_BASE_DIR=/app/storage

# Expose application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=120s --retries=3 \
    CMD /app/healthcheck.sh

# Switch to non-root user
USER skuapp:skuapp

# Set entrypoint
ENTRYPOINT ["/app/entrypoint.sh"]

# Default command
CMD ["java", "org.springframework.boot.loader.JarLauncher"]

# ===================================================================
# Build Instructions:
# ===================================================================
#
# 1. Build the application:
#    mvn clean package -DskipTests
#
# 2. Build the Docker image:
#    docker build \
#      --build-arg BUILD_DATE=$(date -u +'%Y-%m-%dT%H:%M:%SZ') \
#      --build-arg VERSION=1.0.0 \
#      --build-arg GIT_COMMIT=$(git rev-parse HEAD) \
#      -t sku-generator:latest .
#
# 3. Run the container:
#    docker run -d \
#      --name sku-generator \
#      -p 8080:8080 \
#      -e SPRING_PROFILES_ACTIVE=docker \
#      -e DB_HOST=mysql \
#      -e DB_USERNAME=sku_user \
#      -e DB_PASSWORD=sku_password \
#      -v sku-generator-logs:/app/logs \
#      -v sku-generator-storage:/app/storage \
#      sku-generator:latest
#
# ===================================================================