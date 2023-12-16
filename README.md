# Simple photo server

Simple photo server used by [Android photo screensaver](https://github.com/pmarinova/android-photo-screensaver) for TV.

## How to build

The photo server is compiled to a native image with GraalVM.
To build the image you need to set JAVA_HOME to the GraalVM distribution:

`SET "JAVA_HOME=<path-to-graalvm-jdk-21>" & mvn clean package`