#!/bin/bash
set -euo pipefail

launcher="$(readlink -f -- "${1:?Pass the packaged start.sh path}")"
fixture="$(mktemp -d)"
slot=/var/lib/dinosaur-exploder/vms/local-server-900001
link=/var/lib/dinosaur-exploder/vms/local-server-900002
mkdir -p "$slot"
cleanup() {
    rm -f -- "$link" "$slot/unrelated.txt" "$slot/launch-args" \
        "$fixture/start.sh" "$fixture/start-jpro.sh" "$fixture/settings.properties" "$fixture/output"
    rmdir -- "$slot" "$fixture"
}
trap cleanup EXIT
cp "$launcher" "$fixture/start.sh"
printf '#!/bin/bash\nprintf "%%s\\n" "$@" > "$JPRO_WORKING_DIR/launch-args"\n' > "$fixture/start-jpro.sh"

touch "$slot/highScore.ser" "$slot/totalCoins.ser" "$slot/achievements.ser" \
    "$slot/settings.properties" "$slot/unrelated.txt"
JPRO_WORKING_DIR="$slot" bash "$fixture/start.sh" --validation
for file in highScore.ser totalCoins.ser achievements.ser settings.properties; do
    test ! -e "$slot/$file"
done
test -f "$slot/unrelated.txt"
grep -qx -- --validation "$slot/launch-args"
rm "$slot/launch-args"

expect_failure() {
    if "$@" > "$fixture/output" 2>&1; then
        echo "Unsafe worker launch unexpectedly succeeded" >&2
        exit 1
    fi
    test ! -e "$slot/launch-args"
}
expect_failure env -u JPRO_WORKING_DIR bash "$fixture/start.sh"
test ! -e /var/lib/dinosaur-exploder/vms/local-server-900003
expect_failure env JPRO_WORKING_DIR=/var/lib/dinosaur-exploder/vms/local-server-900003 bash "$fixture/start.sh"
touch "$fixture/settings.properties"
expect_failure env JPRO_WORKING_DIR="$fixture" bash "$fixture/start.sh"
test -f "$fixture/settings.properties"
expect_failure env JPRO_WORKING_DIR="${slot}/../local-server-900001" bash "$fixture/start.sh"
ln -s "$fixture" "$link"
expect_failure env JPRO_WORKING_DIR="$link" bash "$fixture/start.sh"
test -f "$fixture/settings.properties"

# A reset failure must not launch a player with retained progress.
mkdir "$slot/settings.properties"
expect_failure env JPRO_WORKING_DIR="$slot" bash "$fixture/start.sh"
rmdir "$slot/settings.properties"
echo "Worker reset, argument forwarding, path guards, and failure handling passed"
