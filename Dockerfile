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

# Installer les dépendances nécessaires pour JavaFX + économiser RAM
RUN apt-get update && apt-get install -y \
    maven \
    curl \
    fontconfig \
    libfreetype6 \
    libpango-1.0-0 \
    libpangoft2-1.0-0 \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/pom.xml .
COPY --from=build /app/target ./target
COPY --from=build /root/. m2/repository/root/.m2/repository
COPY --from=build /app/src ./src

ENV PORT=10000

EXPOSE $PORT

# Configuration mémoire pour Render Free (512MB total)
# Maven: 128MB, JPro/App: 350MB, Système:  ~34MB
CMD sh -c 'echo "=========================================" && \
  echo "🦖 Dinosaur Game - JPro Server" && \
  echo "=========================================" && \
  echo "Port:  ${PORT}" && \
  echo "Memory: Optimized for 512MB" && \
  echo "=========================================" && \
  echo "" && \
  export MAVEN_OPTS="-Xmx128m -XX:+UseSerialGC" && \
  exec mvn jpro:run \
    -Djpro.port=${PORT} \
    -Djpro.openURLOnStartup=false \
    -Djpro.jvmArgs="-Xms128m -Xmx350m -XX:+UseSerialGC -XX:MinHeapFreeRatio=10 -XX:MaxHeapFreeRatio=20 -XX:+UseContainerSupport" \
    -Dprism.order=sw \
    -Dprism.text=t2k \
    -Dprism.poolstats=false \
    -Djavafx.pulseLogger=false \
    -Dprism.verbose=false'