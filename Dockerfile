# ********************************************
# Certificate Server
# Build Image: docker build -f Dockerfile -t ieeeihuserres/certificate-server .
# # ********************************************

FROM maven:3-openjdk-16 as builder
LABEL authors="Iordanis Kostelidis <kostelidis@ieee.org>"

WORKDIR /app

COPY pom.xml ./pom.xml

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn package && rm -fr ~/.m2

FROM openjdk:16-slim as release
LABEL authors="Iordanis Kostelidis <kostelidis@ieee.org>"

COPY ./scripts/run.sh /opt/ieee/ihu/serres/certificate/server/run.sh
COPY --from=builder /app/target/certificate-server-0.0.1-SNAPSHOT.jar /opt/ieee/ihu/serres/certificate/server/mediascouting-print-iiif.jar

RUN chmod +x /opt/ieee/ihu/serres/certificate/server/run.sh

ENV ACTIVE_PROFILE="production" \
    SERVER_PORT="80" \
    EVENT_URL="https://ieeeihuserres.org"

VOLUME ["/srv/ieee/event/"]

ENTRYPOINT ["/opt/ieee/ihu/serres/certificate/server/run.sh"]

