# ─────────────────────────────────────────────
# Etapa 1: BUILD
# Usamos una imagen que ya tiene Java 21 + Maven
# ─────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copiamos primero solo el pom.xml para aprovechar el cache de Docker.
# Si el pom no cambia, Docker no vuelve a bajar todas las dependencias.
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Ahora copiamos el resto del código fuente
COPY src ./src

# Generamos el ZIP de JPro (contiene el server + el juego compilado)
# -DskipTests para no ejecutar los tests durante el build
RUN mvn jpro:release -DskipTests -q

# ─────────────────────────────────────────────
# Etapa 2: RUNTIME
# Imagen más liviana, solo necesita Java para correr
# ─────────────────────────────────────────────
FROM eclipse-temurin:24-jre

WORKDIR /app

# Instalamos las dependencias nativas que necesita JavaFX en Linux
RUN apt-get update && apt-get install -y \
    unzip \
    libpango-1.0-0 \
    libpangoft2-1.0-0 \
    libfreetype6 \
    libfontconfig1 \
    libglib2.0-0 \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/dinosaur-exploder-jpro.zip app.zip
RUN unzip app.zip -d jpro-server && rm app.zip

EXPOSE 8080

CMD ["bash", "-c", "find /app/jpro-server -name 'start.sh' | head -1 | xargs bash"]