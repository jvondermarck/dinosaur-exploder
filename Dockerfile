# ============ BUILD STAGE ============
FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /app

RUN apt-get update && apt-get install -y maven wget && rm -rf /var/lib/apt/lists/*

# Cache des dépendances Maven
COPY pom.xml .
RUN mvn dependency:go-offline -B || true

# Build complet
COPY src ./src
RUN apt-get update && apt-get install -y libgl1-mesa-glx libgtk-3-0 && \
    rm -rf /var/lib/apt/lists/*
RUN mvn clean package -DskipTests

# ============ RUNTIME STAGE ============
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Installer TOUTES les dépendances JavaFX + Audio + GStreamer
RUN apt-get update && apt-get install -y \
    maven \
    wget \
    # JavaFX dependencies
    libgl1-mesa-glx \
    libgtk-3-0 \
    # GStreamer core
    libgstreamer1.0-0 \
    gstreamer1.0-tools \
    gstreamer1.0-x \
    # GStreamer plugins (TOUS)
    gstreamer1.0-plugins-base \
    gstreamer1.0-plugins-good \
    gstreamer1.0-plugins-bad \
    gstreamer1.0-plugins-ugly \
    gstreamer1.0-libav \
    # Librairies GStreamer
    libgstreamer-plugins-base1.0-0 \
    libgstreamer-plugins-good1.0-0 \
    # Codecs audio/vidéo (SANS libavcodec58 pour éviter le conflit)
    libavcodec-extra58 \
    libavformat58 \
    libavutil56 \
    # Audio support (ALSA + PulseAudio)
    gstreamer1.0-alsa \
    gstreamer1.0-pulseaudio \
    pulseaudio \
    alsa-utils \
    libasound2 \
    libasound2-plugins \
    && rm -rf /var/lib/apt/lists/*

# Créer une configuration PulseAudio dummy (pour audio headless)
RUN mkdir -p /root/.config/pulse && \
    echo "load-module module-null-sink sink_name=DummyOutput" > /root/.config/pulse/default.pa && \
    echo "set-default-sink DummyOutput" >> /root/.config/pulse/default. pa && \
    echo "load-module module-native-protocol-unix" >> /root/.config/pulse/default.pa

# Copier les artifacts du build
COPY --from=build /app/pom.xml .
COPY --from=build /app/target ./target
COPY --from=build /root/.m2/repository /root/.m2/repository

# Exposer le port
EXPOSE 8080

# Variables d'environnement
ENV MAVEN_OPTS="-Xmx512m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
# GStreamer debug et configuration
ENV GST_DEBUG=2
ENV GST_PLUGIN_SYSTEM_PATH=/usr/lib/aarch64-linux-gnu/gstreamer-1.0
ENV GST_PLUGIN_PATH=/usr/lib/aarch64-linux-gnu/gstreamer-1.0
# PulseAudio
ENV PULSE_SERVER=unix:/tmp/pulse-socket
# JavaFX Media
ENV JAVAFX_MEDIA_DEBUG=true

# Healthcheck
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/ || exit 1

# Démarrer PulseAudio puis JPro
CMD pulseaudio --start --exit-idle-time=-1 --log-target=stderr && \
    sleep 3 && \
    echo "✅ PulseAudio démarré" && \
    mvn jpro:run
