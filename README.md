# Simple photo server

Simple photo server used by [Android photo screensaver](https://github.com/pmarinova/android-photo-screensaver) for TV.

## How to build

The photo server is compiled to a native image with GraalVM.
To build the native image you need to set JAVA_HOME to the GraalVM distribution:

```
SET "JAVA_HOME=<path-to-graalvm-jdk-21>"
mvn package -Pnative
```

### Building and running a native image with Docker

Build the container image:
```
docker build -t photo-server .
```

Run the image:
```
docker run -d \
-p 40003:40003/tcp \
-p 5353:5353/udp \
--name=photo-server \
photo-server
```

The photo-server docker image exposes TCP port 40003 for the web server and UDP port 5353 for registering the photo-server service with mDNS-SD.
Usually port 5353 will already be bound by another process on the host machine, so either remove the port binding (service discovery will not work)
or run the container using the host network mode:

```
docker run -d \
--net=host \
--name=photo-server \
photo-server \
-p 40003
```

By default, the photo-server image contains a default photos directory. To specify a photos directory from the host, you should mount it to /photos:

```
docker run -d \
-p 40003:40003/tcp \
--mount type=bind,source=<PATH_TO_PHOTOS_DIR>,target=/photos \
--name=photo-server \
photo-server
```

### Building a native image and running as a service on Windows

You can also build a native executable and wrap it as a Windows service:

```
SET "JAVA_HOME=<path-to-graalvm-jdk-21>"
mvn package -Ddist-native
```

On Windows, this will build a native win32 executable and package it with
[Windows Service Wrapper](https://github.com/winsw/winsw).