#!/bin/sh
set -eu

origin="${1:-http://127.0.0.1:8080}"
expected="${2:-false}"
case "$expected" in true|false) ;; *) echo "Expected capacity must be true or false" >&2; exit 1 ;; esac

body="$(curl --fail --silent --show-error --max-time 10 "$origin/_pool/availability")"
test "$body" = "{\"full\":$expected}"
for path in \
    /loadbalancer /loadbalancer/status /jpro/api /jpro/api/instances \
    /app/DinosaurExploderJProWebApp/jpro/api/instances \
    /LOADBALANCER/status /jpro%2fapi/instances \
    /%6coadbalancer/status /app/../loadbalancer/status \
    /_internal/pool-status /_internal/other /_pool/other
do
    code="$(curl --path-as-is --silent --show-error --max-time 5 --output /dev/null \
        --write-out '%{http_code}' "$origin$path")"
    if [ "$code" != 404 ]; then echo "Unblocked management path: $path HTTP $code" >&2; exit 1; fi
done
for path in /%256coadbalancer/status '/loadbalancer;other/status' '/jpro\api/instances'
do
    code="$(curl --path-as-is --silent --show-error --max-time 5 --output /dev/null \
        --write-out '%{http_code}' "$origin$path")"
    if [ "$code" != 400 ]; then echo "Ambiguous path accepted: $path HTTP $code" >&2; exit 1; fi
done
for path in / /busy.html /unavailable.html /pool-ui.js /pool-ui.css /jpro/js/jpro.js /audio/shoot.mp3
do
    curl --fail --silent --show-error --max-time 10 --output /dev/null "$origin$path"
done
echo "Capacity shape, management path guards, and public assets passed"
