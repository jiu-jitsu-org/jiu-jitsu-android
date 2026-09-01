# Module Boundaries

## Allowed Dependencies

```text
:app
  -> feature modules and required core modules for app scaffolding

:feature:* implementation
  -> :core:domain for reusable or combined business logic
  -> :core:data repository APIs for simple operations and events
  -> :core:model for stable app models
  -> :core:ui and :core:designsystem for shared UI

:core:domain
  -> :core:data repository APIs
  -> :core:model

:core:data
  -> :core:model
  -> network and persistence modules when they exist

:core:ui
  -> :core:model
  -> :core:designsystem

:core:designsystem
  -> Android and Compose UI libraries only

:core:model
  -> Kotlin/JVM-compatible libraries only
```

Core modules must not depend on app or feature modules. Do not add feature-to-feature implementation dependencies. Cross-feature navigation may depend on the destination feature's small navigation API after feature `api`/`impl` modules are introduced.

## Responsibilities

### `:app`

- Owns `Application`, `MainActivity`, root navigation composition, app scaffolding, startup wiring, and system integrations.
- Coordinates features without absorbing feature screens or business logic.

### `:core:model`

- Owns stable application-wide models such as profiles, belt ranks, competition ranks, login, and session concepts.
- Must remain free of Retrofit, Moshi, Room, DataStore, Compose, Android-resource, and dependency-injection concerns.
- Network DTOs and database entities are not app models.

### `:core:data`

- Owns repository contracts and implementations, source coordination, synchronization, and mapping at the data boundary.
- Exposes stable app models rather than DTOs, entities, Retrofit wrappers, or UI state.
- Keeps services, DTOs, persistence helpers, implementation-only mappers, and repository implementations `internal` where possible.
- Keeps HTTP error parsing, endpoint envelopes, retries, and transport details inside the data/network boundary.

### `:core:domain`

- Owns focused UseCases that combine repositories, transform streams, validate inputs, or represent reusable business operations.
- May depend on `core:data.repository` contracts and `core:model`.
- Must not import API services, DTOs, response envelopes, DataStore types, `NetworkModule`, repository implementations, or UI state.
- Avoid pass-through UseCases that only rename one repository method.

### `:core:designsystem`

- Owns theme, typography, colors, icons, and generic Compose primitives.
- Must not know app models or depend on data, domain, app, or feature modules.

### `:core:ui`

- Owns app-specific reusable composite UI shared by multiple features.
- May render `:core:model` types and build on `:core:designsystem`.
- Must not depend on `:core:data`.
- Shared model-to-visual mappings may live here. Feature-only mappings stay in the feature.

### `:feature:*`

- Owns screens, ViewModels, feature components, UI models, UI state, and user-event handling.
- May call a repository directly for simple operations.
- Uses a UseCase when logic combines sources, is reused, validates business input, or has meaningful business semantics.
- Converts repository and UseCase output into immutable feature-owned UI state.
- Keeps Route composables responsible for state collection and navigation callbacks; keeps Screen composables stateless where practical.

## Forbidden Upper-Layer Imports

New code in `app`, `core:domain`, `core:ui`, and feature modules must not import:

```text
com.kyu.jiu_jitsu.data.api.*
com.kyu.jiu_jitsu.data.model.dto.*
com.kyu.jiu_jitsu.data.datastore.*
com.kyu.jiu_jitsu.data.module.*
com.kyu.jiu_jitsu.data.repository.impl.*
com.kyu.jiu_jitsu.data.model.singleton.*
```

Existing violations are migration targets, not precedent. When a touched boundary can be corrected without broadening the task, reduce the violation.

## Repository Contracts

Repository contracts remain in `:core:data` under the accepted NIA-style decision. They expose app models and stable results:

```kotlin
interface CommunityRepository {
    fun observeCommunityProfile(): Flow<CommunityProfile>

    suspend fun updateCommunityProfile(
        command: UpdateCommunityProfile,
    ): Result<Unit>
}
```

- Convert DTOs/entities before returning from the repository.
- Avoid `suspend fun ...(): Flow<T>` unless suspension is required to construct the stream.
- Do not wrap a single network response in `flow { emit(...) }` only to represent loading.
- Use a suspending result for one-shot work or a real observable source-of-truth stream for continuing state.

## UI State and Errors

- `UiState` belongs to the owning feature, or to `:core:ui` only for a genuinely shared composite contract.
- Data and domain modules must not return presentation `UiState`.
- ViewModels expose immutable state, preferably through `StateFlow` where continuing observation is useful.
- Loading, empty, content, error, and retry behavior should be explicit when applicable.
- Transport errors are mapped to a stable result before a ViewModel maps them to user-facing state.

## State and Session

- Do not introduce mutable global state in Hilt modules or singleton model objects.
- Replace `ProfileSingleton` with repository-backed observable state or feature ViewModel state.
- Replace module-level auth-token mutation with an injected session/token source observed by the network interceptor.
- Access DataStore and encrypted preferences through repositories or data sources. Domain and feature code must not reference preference keys.

## Navigation

- `:app` owns the root NavHost and composes feature navigation entries.
- Features should own destination keys, navigation functions, and graph registration.
- Do not add new feature destinations to `core:ui/.../routes/AppRoutes.kt` when feature ownership is possible.
- Migrate existing centralized routes incrementally.
- Add feature `api`/`impl` splits only when cross-feature navigation, build isolation, or team ownership justifies the granularity.

