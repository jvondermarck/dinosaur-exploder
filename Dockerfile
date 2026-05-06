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
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copiamos el ZIP generado por JPro desde la etapa de build
COPY --from=build /app/target/jpro/*.zip app.zip

# Descomprimimos el ZIP
RUN apt-get update && apt-get install -y unzip && rm -rf /var/lib/apt/lists/*
RUN unzip app.zip -d jpro-server && rm app.zip

# JPro corre en el puerto 8080 por defecto
EXPOSE 8080

# El script de arranque que genera JPro
# Buscamos el start.sh dentro del directorio descomprimido
CMD ["sh", "-c", "find /app/jpro-server -name 'start.sh' | head -1 | xargs sh"]
