#
# Get the pterodacty egg from https://github.com/PowerNukkit/PowerNukkit-Pterodactyl-Egg!
#
# Prepare the source
FROM alpine/git:v2.26.2 AS prepare

# Copy the source
WORKDIR /src
COPY pom.xml /src

COPY src/main/java /src/src/main/java
COPY src/main/resources /src/src/main/resources

COPY src/test/java/cn /src/src/test/java/cn
COPY src/test/resources /src/src/test/resources

COPY .git /src/.git

# Update the language submodule
RUN if [ -z "$(ls -A /src/src/main/resources/lang)" ]; then git submodule update --init; fi

# Prepare to build the source
FROM maven:3.8.1-jdk-11-slim as build

# Copy the source
WORKDIR /src
COPY --from=prepare /src /src

# Build the source
RUN mvn -Dmaven.javadoc.skip=true -Denforcer.skip=true --no-transfer-progress clean package

# Final image
FROM quay.io/pterodactyl/core:java-11 as pterodactyl

LABEL author="José Roberto de Araújo Júnior" maintainer="joserobjr@powernukkit.org"

USER root
ENV USER=root HOME=/root

RUN mkdir -p /opt/PowerNukkit
COPY --from=build /src/target/powernukkit-*-shaded.jar /opt/PowerNukkit/PowerNukkit.jar

USER container
ENV  USER=container HOME=/home/container
