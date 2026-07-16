# AGENTS.md

## Cursor Cloud specific instructions

MeterSphere V3 monorepo: Spring Boot 3 / Java 21 backend (`backend/app`, port **8081**), Vue 3 + Vite
frontend (`frontend/`, port **5173**), and an optional Node MCP server (`metersphere-mcp/`). Middleware:
MySQL, Redis, Kafka, MinIO. The repo's own dev scripts (`start.ps1`, `scripts/*.ps1`, `dev/env.ps1`) are
**PowerShell/Windows-only**; on the Linux cloud VM run the equivalents below. Extra context lives in
`docs/task/destination/chenqifen-本地开发摘要.md`, `dev/docker-compose.yml`, and `cds-compose.yml`.

The update script only refreshes dependencies. Starting Docker, the middleware, and the app/frontend
servers is **not** automated — do it manually as below.

### 1. Middleware (Docker)
Docker Engine is installed in the snapshot but the daemon does not auto-start. Start it once, then bring up
the required services (Nacos is **optional** — the `local` profile does not use it):
```bash
sudo dockerd >/tmp/dockerd.log 2>&1 &          # only if `docker info` fails
sudo docker compose -f dev/docker-compose.yml up -d mysql redis kafka minio
```
Defaults: MySQL `root/Password123@mysql` db `metersphere` (:3306), Redis `Password123@redis` (:6379),
Kafka (:9092), MinIO `admin/Password123@minio` (:9000/:9001). MySQL schema/seed data is created
automatically by Flyway on first backend boot — no manual SQL import.

### 2. Local runtime config (gitignored, persisted in the snapshot)
The backend `local` profile reads `local-runtime/conf/metersphere.properties` + `redisson.yml` and needs a
JMeter home. This directory is `.gitignore`d and is generated once (already present in the snapshot). If it
is ever missing, recreate it using the connection strings in `cds-compose.yml` (use `127.0.0.1` hosts and
absolute `/workspace/local-runtime/...` paths), and seed the JMeter home from
`backend/app/src/main/resources/jmeter`.

### 3. Backend (Java 21, port 8081)
Run from the **repo root** and target the `backend/app` pom (running against the root reactor fails with
"Unable to find main class"). `-DskipAntRunForJenkins=true` is required in front-end-separated dev so it does
not need `frontend/dist`:
```bash
export MS_CONFIG_DIR=/workspace/local-runtime/conf
export MS_LOG_PATH=/workspace/local-runtime/logs/metersphere
export MS_REDISSON_CONFIG=file:/workspace/local-runtime/conf/redisson.yml
export JMETER_HOME=/workspace/local-runtime/jmeter
./mvnw -f backend/app/pom.xml spring-boot:run -DskipTests -DskipAntRunForJenkins=true -Dspring-boot.run.profiles=local
```
Readiness probe: `GET http://localhost:8081/api/agent/v1/functional/health` returns 200. The root path `/`
returns 500 in dev (no static frontend bundled) — this is expected; use the Vite server for the UI.
Backend build: `./mvnw -pl backend/app -am install -DskipTests -DskipAntRunForJenkins=true`.
Backend tests: `./mvnw -pl <module> test -Dtest=<Class>` (most controller tests boot the full Spring context
and need the middleware running).

### 4. Frontend (Vite dev server, port 5173) — Node 20 only
The project pins **Node v20.8.1** + **pnpm 8.4.0**. nvm's default alias is set to 20.8.1, so a normal login
shell already uses Node 20 (`node -v` → v20.8.1) and plain commands work:
```bash
cd frontend && pnpm run dev          # http://localhost:5173  (proxies /front etc. -> :8081)
```
Do **not** run the frontend on Node 22: the binary `/exec-daemon/node` is v22 and, if invoked directly,
breaks the Vite ESLint plugin with `TypeError: Missing required argument: node` (an eslint 8 + Node 22
incompatibility) — it crashes both `pnpm run dev` (blocking overlay) and `pnpm run lint`. If a shell ever
shows Node 22, force Node 20 first: `. "$NVM_DIR/nvm.sh"; nvm use 20.8.1`.
Lint: `pnpm exec eslint src --ext .vue,.ts,.tsx` (repo `pnpm run lint` adds `--fix`). Type check:
`pnpm run type:check`.

Note: `frontend/package.json` pins `pnpm.overrides.eslint-module-utils` to `2.8.0`. This repo has **no
committed lockfile**, so without the override pnpm resolves `eslint-module-utils@2.14.0`, which crashes
`eslint-plugin-import`'s `import/namespace` rule (the plugin declares `^2.8.0`). Keep this override.

### 5. Login / MCP
Default admin login: `admin` / `metersphere`. `metersphere-mcp/` (Node, uses npm) is an optional Cursor/AI
tool wrapper over the backend Agent API and is not needed for core development.
