#!/bin/bash
set -euo pipefail

: "${POOL_ADMIN_KEY:?Set a non-empty POOL_ADMIN_KEY before starting the browser pool}"
exec java -Dspring.config.location=file:/opt/pool/application.properties \
    -jar /opt/pool/loadbalancer.jar "$@"
