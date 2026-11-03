# Browser hosting with Docker

This is a reproducible JPro browser-hosting prototype related to
[issue #127](https://github.com/jvondermarck/dinosaur-exploder/issues/127).
It does not change the desktop entry point or integrate a play link into the
project website.

## Architecture

JPro runs JavaFX on the server and delivers the game through WebSockets.
The application's player selections and FXGL engine state are static, so
independent games cannot safely share one JVM. The deployment uses JPro
2025.3.3's existing load balancer with one browser game per player JVM.
Two player slots require four JVMs: load balancer, static server, and players.
This is independent single-player hosting, not a shared multiplayer game.

Each worker has a separate working directory. Before its JVM starts, the
guarded launcher resets only the game's four disposable save/settings files.
It rejects missing, noncanonical, traversal, and symlink directories and
aborts on reset failure. Browser progress is temporary; desktop saves retain
their existing behavior.

Browser music and effects use HTML audio instead of a server audio device.
Desktop playback still uses JavaFX media. Static `/audio/` resources avoid
JPro's expiring temporary file-publication permissions. Game/physics
initialization is dispatched to the JavaFX thread in the web entry point,
including restart, with failures propagated to the caller.

The Nginx/njs ingress proxies game traffic, blocks known management route
families, and exposes only a validated capacity boolean. Full pools show a
static busy page with manual retry. Failed capacity telemetry returns HTTP
503/unavailable, not a false capacity answer. JPro owns allocation and
recycling; there is no additional allocator, reservation, or queue.

## Local Linux container setup

Requirements: Docker with a Linux/amd64 engine and Docker Compose.
No host Java, Maven, or audio device is needed. Initial builds download
dependencies and require Internet access.

From the repository root in PowerShell:

```powershell
$env:POOL_ADMIN_KEY=[guid]::NewGuid().ToString('N') + [guid]::NewGuid().ToString('N')
$env:POOL_CAPACITY='2'
docker compose --project-name dinosaur-local --file deploy\compose.yaml up --detach --build
curl.exe --max-time 10 --silent --show-error http://127.0.0.1:8081/_pool/availability
docker compose --project-name dinosaur-local --file deploy\compose.yaml exec -T ingress nginx -t
docker compose --project-name dinosaur-local --file deploy\compose.yaml exec -T ingress sh /etc/nginx/check.sh
```

On Linux/macOS, export the same variables, use slash-separated file paths,
and use `curl` instead of `curl.exe`. Supply a random key privately; do not
reuse a documentation placeholder.

Open <http://127.0.0.1:8081/>. Click to unlock audio, then Start Game, select
ship/weapon, Next, and difficulty. Wait for the countdown. Test movement,
shooting, bombs, pause/resume, and game-over restart. Open another independent
browser context to test isolation; an extra browser when both slots are
occupied should see the busy page.

At startup, capacity can briefly return HTTP 503 until telemetry is ready.
HTTP 200 with `{"full":false}` is a capacity sample, not a reserved slot or
proof that every worker will initialize successfully. While both slots are
occupied, run the full-pool check:

```powershell
docker compose --project-name dinosaur-local --file deploy\compose.yaml exec -T ingress sh /etc/nginx/check.sh http://127.0.0.1:8080 true
```

Inspect resource use with `docker stats`. Runtime diagnostic limits are
4 GiB/four CPUs for the pool and 128 MiB/one CPU for ingress. Image-build
resources are separate from these limits. Do not treat these values as a
measured production sizing recommendation.

To stop, close all game tabs or navigate them to `about:blank` to stop audio:

```powershell
docker compose --project-name dinosaur-local --file deploy\compose.yaml down --timeout 20
Remove-Item Env:\POOL_ADMIN_KEY
Remove-Item Env:\POOL_CAPACITY
```

In a fresh shell, set a temporary nonempty `POOL_ADMIN_KEY` to satisfy Compose
validation before `down`; do not redeploy a live stack with that placeholder.
Stop only this project, not unrelated containers.

## Hosting behind HTTPS

[compose.services.yaml](../../deploy/compose.services.yaml) contains shared,
port-free services. [compose.yaml](../../deploy/compose.yaml) adds the local
loopback test port. The optional
[compose.dokploy.yaml](../../deploy/compose.dokploy.yaml) attaches only ingress
to an existing `dokploy-network`, disables pool Traefik routing, and adds
restart policies and an ingress capacity health check.

In Dokploy, select Docker Compose (not Swarm Stack), use
`./deploy/compose.dokploy.yaml`, and configure a domain for **ingress**, port
**8080**, path `/`, with HTTPS. Do not enable rewriting that attaches the pool
to the public proxy network. Inspect the final generated networks/labels:
the pool must join only the project backend, neither service may publish host
ports, and only ingress may be routed externally.

With other hosting, apply the same separation and configure a trusted HTTPS
reverse proxy with secure WebSocket support. Do not expose the pool directly:
its stock aggregate management routes are unauthenticated even with a key.
Known route blocking is not a comprehensive security audit.

Set `POOL_ADMIN_KEY` privately in the hosting environment and preserve
`POOL_CAPACITY=2` initially. Never print complete container arguments,
environments, or Compose previews with real keys. Environment changes require
container recreation, not merely restarting the old container. Updates and
rollback interrupt active games and reset session-only progress.

Check JPro runtime licensing before public production use. The game's MIT
license does not license the JPro runtime. The
[official pricing page](https://www.jpro.one/pricing) advertises free
development/staging and educational licenses by application; confirm coverage
for the intended public use. This prototype does not establish generally
available free hosting or a licensing exemption.

## Validation and known limitations

Prior prototype testing established Linux/browser audio, restart, two isolated
games, worker handoff with fresh saves, full-pool/retry handling, management
route blocking, and 20 uninterrupted active-play/disconnect cycles on a fresh
local stack. A short three-player test also worked, with five JVMs. These
observations are not sustained capacity or latency results.

A single-session hosted observation averaged approximately 1,378 MiB pool
memory and 0.32 CPU cores over four minutes; memory peaked at 1,664 MiB.
This includes shared JVMs and a warm worker, not just the active game.
Browser focus emulation was used for automation; it is not real-user
performance evidence.

Still open:

- Intermittent replacement-worker startup stalls observed during an earlier
  lifecycle run. Later clean cycles did not reproduce them. The pinned
  status renderer waits on worker futures, so stalls can also block capacity
  telemetry. Capture stalled worker threads before proposing a fix; merely
  raising the ingress timeout does not address worker startup.
- An isolated truncated music response; cause and impact are unconfirmed.
  JPro static audio currently returns complete MP3s even for range requests.
- The planned 15-minute two-session stability test, Firefox, separate browser
  profiles, refresh/reconnect semantics, input latency, and frame pacing.
- An intermittent Windows native startup crash during local `jpro:run`;
  this is separate from the Linux deployment.

Validation commands for this contribution:

```powershell
mvn -B clean package --file pom.xml
docker build --target pool --tag dinosaur-exploder:pool-validation .
docker build --file deploy\ingress\Dockerfile --tag dinosaur-exploder:ingress-validation .
```

The Docker pool build runs all Java tests and worker-launcher shell tests.
The ingress image build runs pinned capacity-parser and handler tests.
For formatting, use the repository's Spotless configuration; CI checks
formatting changes in the Java files touched by the PR.
