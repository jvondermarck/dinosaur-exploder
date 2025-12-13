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

# Dépendances minimales
RUN apt-get update && apt-get install -y \
    maven \
    wget \
    libgl1-mesa-glx \
    libgtk-3-0 \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/pom.xml .
COPY --from=build /app/target ./target
COPY --from=build /root/.m2/repository /root/.m2/repository

# Variables d'environnement pour Render
ENV MAVEN_OPTS="-Xmx1g -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
ENV PORT=8080

EXPOSE $PORT

# Healthcheck pour dire à Render que le service est prêt
HEALTHCHECK --interval=10s --timeout=5s --start-period=120s --retries=10 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT}/ || exit 1

# Commande simple et directe
CMD sh -c "echo '🚀 Starting JPro on port ${PORT}' && mvn jpro:run -Dhttp.port=${PORT}"