# AGENTS.md

## Cursor Cloud specific instructions

MeterSphere V3 is a continuous-testing platform in this repo split into a Spring Boot (Java 21) backend and a Vue 3 / Vite frontend, backed by MySQL, Redis, Kafka, MinIO and Nacos middleware.

### Services

| Service | Location | Dev command | Port |
|---------|----------|-------------|------|
| Backend | `backend/app` (Spring Boot) | see "Run the backend" below | 8081 |
| Frontend | `frontend` (Vite dev server) | `pnpm run dev` (in `frontend`) | 5173 |
| Middleware | `dev/docker-compose.yml` | `sudo docker compose -f dev/docker-compose.yml up -d` | MySQL 3306, Redis 6379, Kafka 9092, MinIO 9000/9001, Nacos 8848 |

The frontend proxies `/front/*` to the backend at `http://localhost:8081` (see `frontend/config/vite.config.dev.ts`), so open the app at `http://localhost:5173`.

The upstream dev scripts (`start.ps1`, `scripts/*.ps1`) are Windows/PowerShell only; on this Linux VM run the equivalent steps below. The local dev architecture is documented in `docs/summary/本地与线上环境对齐改造方案.md` and build steps in `build.md`.

### Startup sequence for a fresh session

The update script only refreshes dependencies. Middleware, Docker daemon and the app servers are NOT auto-started, so start them per session:

1. Start the Docker daemon (Docker is installed in the snapshot but the daemon must be running): `sudo dockerd` (run it in a tmux/background session). It is preconfigured for this VM with `storage-driver: fuse-overlayfs` and `containerd-snapshotter: false` in `/etc/docker/daemon.json` — do not change that or containers will fail to start.
2. Start middleware: `sudo docker compose -f dev/docker-compose.yml up -d` and wait until all containers are `healthy`.
3. Initialize the local runtime config (regenerated because `local-runtime/` is gitignored):
   ```
   mkdir -p local-runtime/conf local-runtime/logs/metersphere local-runtime/jmeter
   cp deploy/nacos/dev/metersphere.properties local-runtime/conf/metersphere.properties
   printf 'singleServerConfig:\n  address: "redis://127.0.0.1:6379"\n  password: "Password123@redis"\n  database: 1\n' > local-runtime/conf/redisson.yml
   ```
4. Build the backend (first build ~15 min; cached in `~/.m2` afterwards):
   ```
   ./mvnw install -N
   ./mvnw -f backend/pom.xml install -pl app -am -DskipTests -DskipAntRunForJenkins=true
   ```
5. Run the backend (uses the `local` Spring profile, which disables Nacos and reads `local-runtime/conf/metersphere.properties`):
   ```
   ./mvnw -f backend/app/pom.xml spring-boot:run -DskipTests -DskipAntRunForJenkins=true \
     -Dspring-boot.run.profiles=local \
     -Dspring-boot.run.jvmArguments=-Dnacos.logging.default.config.enabled=false \
     -Dspring-boot.run.workingDirectory=/workspace
   ```
   Backend is ready when `curl http://127.0.0.1:8081/is-login` returns JSON (HTTP 401 = up, not logged in). Flyway auto-creates the schema and seed data on first run.
6. Run the frontend: `pnpm run dev` in `frontend` (already installed by the update script).

Default login (seeded by Flyway): `admin` / `metersphere`.

### Non-obvious gotchas

- **`./mvnw install -N` is required before a standalone `spring-boot:run`.** Without the root parent pom installed, `spring-boot:run -f backend/app/pom.xml` fails to resolve `org.json:json:${org.json.version}` (the flatten plugin uses `resolveCiFriendliesOnly`, so property interpolation depends on the installed root pom). The update script runs this for you.
- **`-Dspring-boot.run.workingDirectory=/workspace` is required.** `local-runtime/conf/metersphere.properties` uses relative paths (`./local-runtime/...`); the spring-boot plugin otherwise defaults its working dir to `backend/app` and the paths won't resolve.
- **The `local` profile avoids Nacos entirely.** Nacos is started by the compose file but is only needed with the `nacos` profile.
- **Backend integration tests spin up embedded Testcontainers** (MySQL/Redis/Kafka/MinIO images) and require the Docker daemon running. Pure unit tests (e.g. `OrgWecomSyncSecretUtilsTest`) run without it. Run a module's tests with `./mvnw -f backend/pom.xml test -pl services/<module> -Dtest=<Class> -DskipAntRunForJenkins=true`.
- **The frontend has no test runner script**; quality gates are `pnpm run lint` (eslint, note: `--fix` rewrites files) and `pnpm run type:check`. The Vite dev server prints non-fatal eslint warnings and `@arco-design/.../interface ... Are they installed?` deep-import warnings — these are safe to ignore.
- `backend/services/system-setting/.../dto/request/UpdateAuthStatusRequest.java` is required for the backend to compile; if the backend build fails with `cannot find symbol UpdateAuthStatusRequest`, that class is missing.
