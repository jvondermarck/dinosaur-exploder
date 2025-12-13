# ============ BUILD STAGE ============
FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /app

RUN apt-get update && apt-get install -y maven wget && rm -rf /var/lib/apt/lists/*

COPY pom.xml .
RUN mvn dependency:go-offline -B || true

COPY src ./src
RUN apt-get update && apt-get install -y libgl1-mesa-glx libgtk-3-0 && \
    rm -rf /var/lib/apt/lists/*
RUN mvn clean package -DskipTests

# ============ RUNTIME STAGE ============
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

RUN apt-get update && apt-get install -y \
    maven \
    wget \
    curl \
    net-tools \
    libgl1-mesa-glx \
    libgtk-3-0 \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/pom.xml .
COPY --from=build /app/target ./target
COPY --from=build /root/.m2/repository /root/.m2/repository
COPY --from=build /app/src ./src

ENV MAVEN_OPTS="-Xmx1g -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
ENV PORT=10000

EXPOSE $PORT

CMD sh -c 'echo "=========================================" && \
  echo "🦖 Dinosaur Game - JPro Server" && \
  echo "=========================================" && \
  echo "Port:  ${PORT}" && \
  echo "=========================================" && \
  echo "" && \
  exec mvn jpro:run -Djpro.port=${PORT}'