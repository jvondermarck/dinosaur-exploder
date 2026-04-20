#!/usr/bin/env bash
set -euo pipefail

# Lightweight Maven wrapper that installs Maven locally (no sudo) into .mvn/maven/

MVNW_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MAVEN_VERSION="3.9.9"
MAVEN_DIR="$MVNW_DIR/.mvn/maven/apache-maven-$MAVEN_VERSION"
MAVEN_BIN="$MAVEN_DIR/bin/mvn"

# Allow overriding the download URL (useful behind proxies/firewalls)
MAVEN_TGZ_URL_DEFAULT="https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$MAVEN_VERSION/apache-maven-$MAVEN_VERSION-bin.tar.gz"
MAVEN_TGZ_URL="${MAVEN_TGZ_URL:-$MAVEN_TGZ_URL_DEFAULT}"

ensure_maven() {
  if [[ -x "$MAVEN_BIN" ]]; then
    return 0
  fi

  mkdir -p "$MVNW_DIR/.mvn/maven"

  local tgz="$MVNW_DIR/.mvn/maven/apache-maven-$MAVEN_VERSION-bin.tar.gz"

  if [[ ! -f "$tgz" ]]; then
    echo "Downloading Maven $MAVEN_VERSION..." >&2
    urls=(
      "$MAVEN_TGZ_URL"
      "https://downloads.apache.org/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz"
      "https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz"
    )

    downloaded=0
    for url in "${urls[@]}"; do
      if command -v curl >/dev/null 2>&1; then
        if curl -fsSL --connect-timeout 10 --retry 2 --retry-delay 1 "$url" -o "$tgz"; then
          downloaded=1
          break
        fi
      elif command -v wget >/dev/null 2>&1; then
        if wget -q --timeout=20 "$url" -O "$tgz"; then
          downloaded=1
          break
        fi
      else
        echo "Error: need curl or wget to download Maven" >&2
        exit 2
      fi
    done

    if [[ "$downloaded" -ne 1 ]]; then
      echo "Error: couldn't download Maven. Try setting MAVEN_TGZ_URL to a reachable URL." >&2
      exit 4
    fi
  fi

  echo "Installing Maven $MAVEN_VERSION locally..." >&2
  tar -xzf "$tgz" -C "$MVNW_DIR/.mvn/maven"

  if [[ ! -x "$MAVEN_BIN" ]]; then
    echo "Error: Maven install failed; expected $MAVEN_BIN" >&2
    exit 3
  fi
}

ensure_maven
exec "$MAVEN_BIN" -f "$MVNW_DIR/pom.xml" "$@"
