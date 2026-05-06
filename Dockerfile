# ─────────────────────────────────────────────
# Etapa 1: BUILD dentro del contenedor
# ─────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-24 AS build

WORKDIR /app

# Copiamos el pom.xml primero para cachear dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copiamos el código fuente
COPY src ./src

# Compilamos el ZIP de JPro directamente en Java 24
RUN mvn jpro:release -DskipTests -q

# ─────────────────────────────────────────────
# Etapa 2: RUNTIME
# ─────────────────────────────────────────────
FROM eclipse-temurin:24-jre

WORKDIR /app

# Instalamos dependencias nativas de JavaFX
RUN apt-get update && apt-get install -y \
    unzip \
    libpango-1.0-0 \
    libpangoft2-1.0-0 \
    libfreetype6 \
    libfontconfig1 \
    libglib2.0-0 \
    && rm -rf /var/lib/apt/lists/*

# Copiamos el ZIP generado en la etapa de build
COPY --from=build /app/target/dinosaur-exploder-jpro.zip app.zip
RUN unzip app.zip -d jpro-server && rm app.zip

EXPOSE 8080

CMD ["bash", "-c", "find /app/jpro-server -name 'start.sh' | head -1 | xargs bash"]