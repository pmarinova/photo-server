# Using Oracle GraalVM for JDK 21
FROM container-registry.oracle.com/graalvm/native-image:21-muslib AS builder

# Install xargs as part of the findutils package (required by gradlew)
RUN microdnf install findutils

# Set the working directory
WORKDIR /build

# Copy the source code into the image for building
COPY src ./src
COPY gradle ./gradle
COPY build.gradle .
COPY gradlew .
COPY settings.gradle .

# Build
RUN ./gradlew nativeCompile -Pstatic

# Setup UPX
RUN microdnf install wget xz
COPY setup-upx.sh .
RUN chmod +x setup-upx.sh
RUN bash setup-upx.sh

# Create a compressed version of the executable
RUN ./upx --lzma --best build/native/nativeCompile/photo-server -o photo-server.upx

# The deployment image
FROM scratch

EXPOSE 40003/tcp
EXPOSE 5353/udp

# Copy the native executable into the container
COPY --from=builder /build/photo-server.upx photo-server

# Copy the default photos directory
COPY photos photos

ENTRYPOINT ["/photo-server"]