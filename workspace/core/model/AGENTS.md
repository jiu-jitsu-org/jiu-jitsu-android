# `:core:model` Agent Guide

This guide applies to everything under `core/model/`. It extends the repository-root
[`AGENTS.md`](../../AGENTS.md); the root guide and accepted ADRs win if instructions conflict.

## Purpose and Boundary

- This is the framework-independent source of stable application-wide models, commands, value
  objects, and operation results shared across layers.
- Keep the module Kotlin/JVM-only. Do not add Android SDK, Compose, resource IDs, lifecycle, Hilt,
  Retrofit, Moshi, Room, DataStore, parcelization, or transport/persistence annotations.
- An API DTO, database entity, preference record, composable state, or navigation argument is not an
  app model. Map those representations at their owning boundary.
- Add a type here only when it expresses stable application meaning and is shared across layers or
  features. Feature-only form state, selection state, and display models stay in the feature.

## Model Design

- Prefer immutable `data class`, `enum class`, sealed hierarchy, and value-object designs with `val`
  properties and read-only collections.
- Model nullability semantically. Use `null` only for a meaningful absence; do not copy DTO nullability
  into app models by default.
- Keep defaults conservative. A default must represent a valid domain value, not merely make mapping
  or construction compile.
- Keep transport field names, HTTP codes, preference keys, image resource IDs, colors, and localized
  display strings out of new models.
- Existing rank/style display strings are migration debt, not precedent. New or touched display text
  should be represented semantically and mapped to resources in UI-owned code.
- When the backend can introduce values independently, define and test an explicit unknown/fallback
  mapping at the data boundary rather than calling `enumValueOf` in upper layers.
- Keep derived properties deterministic and side-effect free. Business operations that need
  repositories or environment belong in `:core:domain` or `:core:data`, not model constructors.
- Avoid generic catch-all models and ambiguous maps. Prefer named properties and types that preserve
  invariants at call sites.

## Compatibility and Changes

- Treat public model changes as cross-module contract changes. Search all constructors, mappings,
  reducers, previews, and tests before editing a property or enum.
- Prefer additive migration when a model is widely consumed. If a breaking change is necessary,
  update all producers and consumers in the same change.
- Do not make models serializable merely for convenience. Serialization belongs to the transport or
  persistence representation unless the serialization format itself is an accepted app contract.
- Update architecture documentation when ownership or meaning changes; update API/storage contract
  documentation when the change originates from a backend or persisted contract.

## Results and Commands

- `AppResult` represents a stable cross-layer operation outcome. Keep transport exceptions, Retrofit
  responses, and server envelopes behind the data boundary.
- Commands such as profile updates should contain semantic app values, not form widgets, resource IDs,
  URIs tied to UI launchers, or DTOs.
- Avoid placing presentation loading/error/empty state in this module. Feature-owned immutable
  `UiState` remains in the feature.

## Tests and Verification

- Test equality/copy semantics, defaults, derived properties, value invariants, and unknown/fallback
  behavior affected by the change.
- Pure model tests must remain fast JVM tests with no Robolectric or Android instrumentation.

Run from the Gradle root (`workspace/`):

```bash
./gradlew :core:model:test :core:model:compileKotlin --no-daemon
bash scripts/check-architecture.sh
```

Compile all affected producers and consumers after a public model change.
