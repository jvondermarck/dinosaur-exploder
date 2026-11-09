#!/bin/bash
set -euo pipefail

: "${JPRO_WORKING_DIR:?JPro must supply a worker working directory}"
prefix=/var/lib/dinosaur-exploder/vms/local-server-
case "$JPRO_WORKING_DIR" in
    "$prefix"*) slot="${JPRO_WORKING_DIR#"$prefix"}" ;;
    *) echo "Refusing to reset files outside the browser worker pool" >&2; exit 1 ;;
esac
case "$slot" in
    ''|*[!0-9]*) echo "Invalid browser worker slot" >&2; exit 1 ;;
esac
if [[ ! -d "$JPRO_WORKING_DIR" || "$(readlink -f -- "$JPRO_WORKING_DIR")" != "$JPRO_WORKING_DIR" ]]; then
    echo "Refusing to reset a missing or non-canonical browser worker directory" >&2
    exit 1
fi

# Reset only this game's disposable files, after the previous worker has exited.
rm -f -- \
    "$JPRO_WORKING_DIR/highScore.ser" \
    "$JPRO_WORKING_DIR/totalCoins.ser" \
    "$JPRO_WORKING_DIR/achievements.ser" \
    "$JPRO_WORKING_DIR/settings.properties"
echo "Reset browser progress for worker $slot"
exec bash "$(dirname "$0")/start-jpro.sh" "$@"
