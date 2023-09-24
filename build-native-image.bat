@echo off

SET JAVA_HOME=C:\Java\graalvm-jdk-17.0.8+9.1
mvn clean package -e -Pnative

pause