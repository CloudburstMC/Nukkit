# This Dockerfile uses Docker Multi-Stage Builds
# See https://docs.docker.com/engine/userguide/eng-image/multistage-build/
# Requires Docker v17.05

# Use OpenJDK JDK image for intermiediate build
FROM openjdk:8-jdk-slim AS build

# Install packages required for build
RUN apt-get -y update 
RUN apt-get install -y build-essential
RUN apt-get install -y git
RUN mkdir -p /usr/share/man/man1
RUN apt-get install -y maven

# Build from source and create artifact
WORKDIR /src

COPY mvn* pom.xml /src/
COPY src /src/src
COPY .git /src/.git
COPY .mvn /src/.mvn

RUN git submodule update --init
RUN mvn clean package

# Use OpenJDK JRE image for runtime
FROM openjdk:8-jre-slim AS run
LABEL maintainer="Micheal Waltz <dockerfiles@ecliptik.com>"

# Copy artifact from build image
COPY --from=build /src/target/nukkit-1.0-SNAPSHOT.jar /app/nukkit.jar

# Create minecraft user
RUN useradd --user-group \
            --no-create-home \
            --home-dir /data \
            --shell /usr/sbin/nologin \
            minecraft

# Ports
EXPOSE 19132

RUN mkdir /data && mkdir /home/minecraft
RUN chown -R minecraft:minecraft /app /data /home/minecraft

# User and group to run as
USER minecraft:minecraft

# Volumes
VOLUME /data /home/minecraft

# Set runtime workdir
WORKDIR /data

# Run app
ENTRYPOINT ["java"]
CMD [ "-jar", "/app/nukkit.jar" ]
