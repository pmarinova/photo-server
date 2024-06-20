# Using Oracle GraalVM for JDK 21
FROM container-registry.oracle.com/graalvm/native-image:21-ol9 AS builder

# Set the working directory
WORKDIR /build

# Copy the source code into the image for building
COPY src ./src
COPY .mvn ./.mvn
COPY mvnw .
COPY pom.xml .

# Build
RUN ./mvnw --no-transfer-progress native:compile -Pnative

# The deployment image
FROM container-registry.oracle.com/os/oraclelinux:9-slim

EXPOSE 40003/tcp
EXPOSE 5353/udp

# Copy the native executable into the container
COPY --from=builder /build/target/photo-server photo-server

# Copy the default photos directory
COPY photos photos

ENTRYPOINT ["/photo-server"]