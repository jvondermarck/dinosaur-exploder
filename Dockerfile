# syntax=docker/dockerfile:1
FROM maven:3.9-eclipse-temurin-21@sha256:99e61abcff91a9b1333463bd8451fb18495d6eba9250ac66a338b518f8278320 AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B clean package jpro:release -Djpro.releasePlatforms=linux
RUN mkdir /release && cd /release && jar xf /build/target/dinosaur-exploder-jpro.zip

FROM build AS pool-release
RUN apt-get update && apt-get install -y --no-install-recommends zip \
    && rm -rf /var/lib/apt/lists/*
COPY deploy/jpro/worker-start.sh /worker-start.sh
COPY deploy/jpro/worker-start-test.sh /worker-start-test.sh
RUN cd /release/dinosaur-exploder-jpro/bin \
    && mv start.sh start-jpro.sh \
    && sed -i 's/^"$JAVA_CMD" \\/exec "$JAVA_CMD" \\/' start-jpro.sh \
    && grep -q '^exec "$JAVA_CMD"' start-jpro.sh \
    && cp /worker-start.sh start.sh \
    && sed -i 's/\r$//' start.sh \
    && bash -n start.sh \
    && sed -i 's/\r$//' /worker-start-test.sh \
    && bash /worker-start-test.sh "$PWD/start.sh" \
    && chmod 755 ./*.sh \
    && mkdir /pool-release \
    && cd /release \
    && zip -qr /pool-release/game.zip dinosaur-exploder-jpro

FROM eclipse-temurin:21-jre-jammy@sha256:1eac8b8fee11ca7d6725a48bc5d41992ed1561504ebae25490d2d062847e8269 AS runtime-base
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
       libgtk-3-0 libxtst6 libxi6 libxrender1 libasound2 libgl1 fonts-dejavu-core \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --gid 10001 game \
    && useradd --uid 10001 --gid game --create-home game \
    && mkdir /var/lib/dinosaur-exploder \
    && chown game:game /var/lib/dinosaur-exploder
USER game
WORKDIR /var/lib/dinosaur-exploder
EXPOSE 8080

FROM runtime-base AS pool
USER root
ADD --checksum=sha256:2808c981deb8b24fec6470e4bfcaa5c2cb891cb0502d8c75612b4009f74f532e \
    https://sandec.jfrog.io/artifactory/repo/one/jpro/jpro-loadbalancer/2025.3.3/jpro-loadbalancer-2025.3.3.jar /opt/pool/loadbalancer.jar
COPY --from=pool-release /pool-release/game.zip /opt/pool/game.zip
COPY deploy/jpro/application.properties /opt/pool/application.properties
COPY deploy/jpro/pool-start.sh /opt/pool/start.sh
RUN sed -i 's/\r$//' /opt/pool/start.sh \
    && bash -n /opt/pool/start.sh \
    && chmod 755 /opt/pool \
    && chmod 444 /opt/pool/loadbalancer.jar /opt/pool/game.zip /opt/pool/application.properties /opt/pool/start.sh
USER game
ENTRYPOINT ["bash", "/opt/pool/start.sh"]

FROM runtime-base AS runtime
USER root
COPY --from=build /release/dinosaur-exploder-jpro /opt/dinosaur-exploder
# The generated launcher otherwise leaves Bash between Docker and the JVM.
RUN sed -i 's/^"$JAVA_CMD" \\/exec "$JAVA_CMD" \\/' /opt/dinosaur-exploder/bin/start.sh \
    && grep -q '^exec "$JAVA_CMD"' /opt/dinosaur-exploder/bin/start.sh
USER game
ENV JPRO_WORKING_DIR=/var/lib/dinosaur-exploder
ENTRYPOINT ["bash", "/opt/dinosaur-exploder/bin/start.sh"]
CMD ["-Djpro.port=8080", "-Djpro.openURLOnStartup=false"]
