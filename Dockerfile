# ********************************************
# Certificate Server
# Build Image: docker build -f Dockerfile -t ieeeihuserres/certificate-server .
# # ********************************************

FROM maven:3-openjdk-8 as builder

WORKDIR /app

COPY pom.xml ./pom.xml

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn package && rm -fr ~/.m2

FROM openjdk:8-slim as release
LABEL MAINTAINER="Iordanis Kostelidis <kostelidis@ieee.org>"

COPY ./scripts/run.sh /opt/ieee/ihu/serres/certificate/server/run.sh
COPY --from=builder /app/target/certificate-server-0.0.1-SNAPSHOT.jar /opt/ieee/ihu/serres/certificate/server/certificate-server.jar

RUN chmod +x /opt/ieee/ihu/serres/certificate/server/run.sh

WORKDIR /srv/ieee/event/
VOLUME ["/srv/ieee/event"]

COPY ./src/main/resources/default/certificate.pdf .
COPY ./src/main/resources/default/participants.csv .

ENV EVENT_URL="https://ieeeihuserres.org"

ENTRYPOINT ["/opt/ieee/ihu/serres/certificate/server/run.sh"]

