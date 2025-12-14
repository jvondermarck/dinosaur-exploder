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
    curl \
    fontconfig \
    libfreetype6 \
    libpango-1.0-0 \
    libpangoft2-1.0-0 \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/pom.xml .
COPY --from=build /app/target ./target
COPY --from=build /root/.m2/repository /root/.m2/repository
COPY --from=build /app/src ./src

ENV PORT=10000

EXPOSE $PORT

# Optimisé pour 512MB avec fix du timeout Maven
CMD sh -c 'echo "=========================================" && \
  echo "🦖 Dinosaur Game - JPro Server" && \
  echo "=========================================" && \
  echo "Port: ${PORT}" && \
  echo "=========================================" && \
  export MAVEN_OPTS="-Xmx96m -XX:+UseSerialGC -XX:TieredStopAtLevel=1" && \
  (mvn jpro:run \
    -Djpro.port=${PORT} \
    -Djpro.openURLOnStartup=false \
    -Djpro.jvmArgs="-Xms256m -Xmx400m -XX:+UseSerialGC -XX:+UseContainerSupport -XX:TieredStopAtLevel=1 -Djava.awt.headless=true -Dmonocle.platform=Headless -Dglass.platform=Monocle -Dprism.order=sw -Dprism.text=t2k" \
    > /tmp/jpro.log 2>&1 & \
  echo $! > /tmp/jpro.pid && \
  tail -f /tmp/jpro.log)'