# `:core:data` Agent Guide

This guide applies to everything under `core/data/`. It extends the repository-root
[`AGENTS.md`](../../AGENTS.md); the root guide and accepted ADRs win if instructions conflict.

## Read Before Editing

- Read [module boundaries](../../docs/architecture/module-boundaries.md),
  [API and storage contracts](../../docs/architecture/contracts.md), and the active
  [NIA migration](../../docs/workstreams/nia-architecture-migration.md).
- Inspect the affected repository contract, its implementation, service/DTO, mapper, Hilt binding,
  and focused tests as one change surface.
- Treat Retrofit interfaces and DTOs as executable transport contracts, but do not expose them as
  the public data-layer contract.

## Module Contract

- This module owns repository contracts and implementations, remote/local data sources, transport
  and persistence mapping, synchronization, session state, and data-layer dependency injection.
- Public repository APIs expose only stable `:core:model` types, `AppResult`, Kotlin primitives,
  and genuine observable source-of-truth streams.
- Services, DTOs, endpoint envelopes, preference keys, crypto helpers, interceptors, mappers, data
  sources, and repository implementations are implementation details. Keep them `internal` where
  Hilt and test visibility allow it.
- Do not return Compose state, feature `UiState`, Android resources, Retrofit `Response`, DTOs,
  DataStore preferences, or implementation classes to domain, app, or feature modules.
- Map transport and persistence representations at this boundary. Do not make upper layers repair
  nullability, translate server enums, inspect HTTP codes, or unwrap endpoint envelopes.

## Repository API Design

- Use a suspending `AppResult<T>` for one-shot reads, writes, commands, and events.
- Use `Flow<T>` or `StateFlow<T>` only for continuing state that can emit again. Do not wrap one
  network response in `flow { emit(...) }` to simulate loading.
- A flow-producing function should normally be non-suspending and named `observe...` when it starts
  or exposes continuing observation.
- Keep each repository contract narrow and capability-based. Do not expose an implementation only
  so a consumer can bypass the contract.
- Update repository-owned state only after the corresponding operation succeeds, unless optimistic
  behavior is an explicit product requirement with rollback semantics.
- Preserve coroutine cancellation. Do not convert `CancellationException` into an application error,
  and do not add unscoped/global coroutines.
- Avoid duplicate sources of truth. If memory, encrypted preferences, and network state coexist,
  document which one is authoritative and synchronize through the owning repository/data source.

## Network and Mapping Rules

- Keep endpoint paths, authentication client selection, request/response shape, and optional-field
  semantics aligned with [contracts.md](../../docs/architecture/contracts.md).
- Add or change a DTO separately from its app model. Use explicit mapping functions; do not rely on
  matching field names as an implicit boundary.
- Define behavior for null, blank, unknown enum, missing field, and server-error cases affected by a
  contract change. Prefer safe fallback or an explicit stable failure over an uncaught mapper crash.
- Keep retry, HTTP error parsing, and transport-envelope handling inside this module. Do not retry
  non-idempotent operations unless the backend contract makes it safe.
- Do not add ad hoc body/header logging to authenticated, token-refresh, or upload clients.
  `OkHttpProfilerInterceptor` is the intentional local-debug exception described below.
- Update `docs/architecture/contracts.md` in the same change when an endpoint, payload family,
  authentication rule, timeout, or persisted key changes.

## OkHttp Profiler Policy

- Keep `io.nerdythings:okhttp-profiler` declared through `libs.okhttp.profiler` in this module. The
  profiler is the project's standard tool for validating and monitoring network requests and
  responses during development; do not remove it as unused logging infrastructure.
- Keep `OkHttpProfilerInterceptor` attached to every app-owned `OkHttpClient` constructed by
  `NetworkModule`. When adding a client, call `addProfilerInterceptorIfDebug()` after its functional
  interceptors and before `build()` so the profiler observes the final request and response.
- Preserve the `BuildConfig.DEBUG` guard inside `addProfilerInterceptorIfDebug()`. Never enable the
  profiler unconditionally or in release builds.
- Do not replace the shared helper with duplicated conditionals or one-off profiler instances. Keep
  client transport defaults in `commonClientBuilder()` and profiler activation in the dedicated
  helper.
- Profiler output can contain authorization headers, refresh data, request bodies, responses, and
  personal data. Use it only for local debugging, do not attach profiler/Logcat output to shared
  reports, and do not commit captured output.
- The Android Studio OkHttp Profiler plugin is a developer-machine requirement, not a repository
  dependency. Document setup changes in `docs/development/index.md`.

## Session, Persistence, and Security

- `SessionRepository` is the public session boundary. Features must not read preference keys or
  mutate `AccessTokenProvider`, `SessionLocalDataSource`, or Hilt modules directly.
- Keep access/refresh tokens encrypted at rest and absent from logs, exception messages, fixtures,
  screenshots, and documentation.
- Preserve the serialized refresh behavior and retry-at-most-once invariant in
  `TokenRefreshInterceptor`; test concurrent refresh behavior when touching it.
- Clear account-scoped in-memory state when a session starts, switches, expires, or is cleared so
  data from the prior user cannot flash or leak.
- Treat persisted-key changes as migrations. Preserve existing users' data or document and test the
  intentional reset behavior.
- Never print values loaded from `local.properties`, Firebase files, signing files, or environment
  variables. Tests must use obvious fake values.

## Dependency and DI Rules

- `:core:model` is the only current core dependency that belongs in this module's public model
  boundary. Do not add dependencies on `:core:domain`, `:core:ui`, `:app`, or a feature module.
- Bind interfaces to implementations in Hilt modules; prefer constructor injection and immutable
  dependencies.
- Do not store mutable application state in Hilt provider objects or Kotlin `object` singletons.
- Add libraries through `gradle/libs.versions.toml`; use `api` only when a type is intentionally part
  of the repository public API.

## Tests and Verification

Add focused tests for changed behavior, especially:

- DTO-to-model mapping, nullable fields, and unknown enum values.
- Repository success/failure behavior and source-of-truth updates.
- Session start/clear, token refresh races, retry limits, and authorization-header omission.
- Persistence encryption, key migration, and account-state clearing when those areas change.

Run from the Gradle root (`workspace/`):

```bash
./gradlew \
  :core:data:testDebugUnitTest \
  :core:data:compileDebugKotlin \
  :core:data:compileReleaseKotlin \
  --no-daemon
bash scripts/check-architecture.sh
```

Also compile affected consumers when a repository contract changes. Report any verification blocked
by missing local configuration without revealing its values.
