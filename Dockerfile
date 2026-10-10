# syntax=docker/dockerfile:1

# ---------- Build ----------
FROM maven:3.10.0-amazoncorretto-11-debian-trixie AS builder

WORKDIR /app

# Resolve dependencies first so they are cached until pom.xml changes
COPY pom.xml ./
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -q dependency:go-offline

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B package \
 && cp target/certificate-server-*.jar app.jar \
 && java -Djarmode=layertools -jar app.jar extract --destination extracted

# ---------- Runtime ----------
# Minimal Java runtime with only the modules the app needs (musl build, matches the Alpine release image)
FROM amazoncorretto:11-alpine3.23 AS runtime
RUN apk add --no-cache binutils \
 && jlink \
      --add-modules java.base,java.desktop,java.instrument,java.logging,java.management,java.naming,java.net.http,java.scripting,java.security.jgss,java.sql,java.xml,jdk.crypto.ec,jdk.unsupported,jdk.zipfs \
      --strip-debug --no-man-pages --no-header-files --compress=2 \
      --output /jre

# ---------- Release ----------
FROM alpine:3.23 AS release
LABEL org.opencontainers.image.title="certificate-server" \
      org.opencontainers.image.authors="Iordanis Kostelidis <kostelidis@ieee.org>" \
      org.opencontainers.image.source="https://github.com/IEEEIHUSerres/certificate-server"

RUN addgroup -S -g 10001 app \
 && adduser -S -D -H -u 10001 -G app -s /sbin/nologin app

ENV JAVA_HOME=/opt/java
ENV PATH="$JAVA_HOME/bin:$PATH"
COPY --from=runtime /jre $JAVA_HOME

WORKDIR /opt/ieee/ihu/serres/certificate/server

# Spring Boot layers, least to most frequently changing, for better layer reuse
COPY --from=builder /app/extracted/dependencies/ ./
COPY --from=builder /app/extracted/spring-boot-loader/ ./
COPY --from=builder /app/extracted/snapshot-dependencies/ ./
COPY --from=builder /app/extracted/application/ ./

# Default event data; a named volume is seeded with these, a bind mount replaces them
COPY --chown=app:app ./src/main/resources/default/certificate.pdf ./src/main/resources/default/participants.csv /srv/ieee/event/
VOLUME ["/srv/ieee/event"]

ENV EVENT_URL="https://ieeeihuserres.org" \
    SERVER_PORT=8080 \
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError -Djava.security.egd=file:/dev/./urandom"

USER app:app
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
    CMD nc -z 127.0.0.1 "$SERVER_PORT" || exit 1

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
