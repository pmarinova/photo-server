# Using Oracle GraalVM for JDK 21
FROM container-registry.oracle.com/graalvm/native-image:21-ol9 AS builder

# Set the working directory
WORKDIR /build

# Copy the source code into the image for building
COPY . /build

# Build
RUN ./mvnw --no-transfer-progress native:compile -Pnative

# The deployment image
FROM container-registry.oracle.com/os/oraclelinux:9-slim

EXPOSE 40003

# Copy the native executable into the container
COPY --from=builder /build/target/photo-server photo-server
COPY --from=builder /build/photos photos
ENTRYPOINT ["/photo-server"]