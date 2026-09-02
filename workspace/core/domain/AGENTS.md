# `:core:domain` Agent Guide

This guide applies to everything under `core/domain/`. It extends the repository-root
[`AGENTS.md`](../../AGENTS.md); the root guide and accepted ADRs win if instructions conflict.

## Purpose and Boundary

- Keep this a framework-independent Kotlin/JVM module for reusable business validation,
  transformation, and meaningful operations.
- A UseCase is justified when it combines repositories or streams, enforces reusable business rules,
  transforms data meaningfully, or names a business operation whose behavior is independently tested.
- Do not add a pass-through UseCase that only renames one repository call. A feature ViewModel may
  call a `:core:data` repository contract directly for a simple read, write, toggle, or event.
- Repository contracts remain owned by `:core:data` per
  [ADR 0002](../../docs/architecture/decisions/0002-keep-repository-contracts-in-core-data.md).
  Add a domain-to-data dependency only when a real UseCase needs a repository contract.

## Allowed Types

- Prefer Kotlin primitives, immutable collections, `:core:model` app models, and narrow repository
  contracts as inputs and outputs.
- Keep Android SDK, Compose, resources, lifecycle, Hilt annotations, Retrofit/Moshi, DTOs, DataStore,
  repository implementations, and presentation `UiState` out of this module.
- Do not encode localized display text, colors, icons, navigation, or screen state in domain results.
  Return a semantic value or reason that the owning feature can map to resources and UI.
- Use injected clocks, dispatchers, random/id generators, or policy interfaces when time or environment
  affects a rule; deterministic domain tests must not depend on the device.

## UseCase and Validation Design

- Give each UseCase one primary responsibility and expose it through `operator fun invoke(...)` when
  that keeps the call site clear.
- Make validation normalization explicit: distinguish raw input, trimmed/canonical input, validation
  failure, and the value sent to the repository. Do not silently apply inconsistent normalization in
  multiple features.
- Represent expected business outcomes with explicit sealed types, value objects, or stable
  `AppResult` values. Do not throw exceptions for ordinary validation failures.
- Preserve coroutine cancellation and flow cold/hot semantics. Avoid hidden scopes and mutable global
  caches.
- For combined flows, define initial/loading behavior, distinctness, and error propagation. Do not
  repeatedly trigger writes as a side effect of collection.
- Keep extensions focused and discoverable. If an extension carries business policy, test it and name
  the file for that policy rather than accumulating unrelated helpers in `Ext.kt`.

## Dependencies

- Keep the dependency surface minimal. `:core:model` and public contracts from `:core:data` are the
  only expected project dependencies.
- Never depend on `:core:ui`, `:app`, or feature modules.
- Add libraries through `gradle/libs.versions.toml`; prefer the Kotlin/JVM artifact when a library
  offers both JVM and Android variants.

## Tests and Verification

- Unit-test boundary values, blank/Unicode input, normalization, valid/invalid partitions, unknown
  enum values, and ordering/date limits relevant to the changed rule.
- Test each stream combination with controlled fakes and verify emissions, cancellation, and failure
  behavior. Avoid Android instrumentation for pure domain behavior.
- When adding a UseCase, tests must demonstrate the business value beyond delegation.

Run from the Gradle root (`workspace/`):

```bash
./gradlew :core:domain:test :core:domain:compileKotlin --no-daemon
bash scripts/check-architecture.sh
```

Compile affected feature modules too when a public domain API changes.
