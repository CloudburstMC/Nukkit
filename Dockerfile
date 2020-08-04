# This Dockerfile uses Docker Multi-Stage Builds
# See https://docs.docker.com/engine/userguide/eng-image/multistage-build/
# Requires Docker v17.05

# Prepare the source
FROM alpine/git:v2.26.2 AS prepare

# Copy the source
WORKDIR /src
COPY ./ /src

# Update the language submodule
RUN if [ -z "$(ls -A /src/src/main/resources/lang)" ]; then git submodule update --init; fi

# Prepare to build the source
FROM maven:3.6-jdk-8-alpine as build

# Copy the source
WORKDIR /src
COPY --from=prepare /src /src

# Build the source
RUN mvn clean package

# Use OpenJDK JRE image to runtime
FROM openjdk:8-jre-slim AS run
LABEL maintainer="José Roberto de Araújo Júnior <joserobjr@powernukkit.org>"

# Copy artifact from build image
COPY --from=build /src/target/powernukkit-*-shaded.jar /app/powernukkit.jar

# Create minecraft user
RUN useradd --user-group \
            --no-create-home \
            --home-dir /data \
            --shell /usr/sbin/nologin \
            minecraft

# Ports
EXPOSE 19132/udp

# Make app owned by minecraft user
RUN mkdir /data && chown -R minecraft:minecraft /app /data

# Volumes
VOLUME /data /home/minecraft

# User and group to run as
USER minecraft:minecraft

# Set runtime workdir
WORKDIR /data

# Run app
CMD [ "java", "-jar", "/app/powernukkit.jar" ]
