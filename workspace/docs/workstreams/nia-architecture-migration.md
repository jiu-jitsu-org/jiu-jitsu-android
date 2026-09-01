# NIA Architecture Migration

- Status: Active
- Started: 2026-09-01
- Decisions: [ADR 0001](../architecture/decisions/0001-adopt-nia-style-architecture.md), [ADR 0002](../architecture/decisions/0002-keep-repository-contracts-in-core-data.md)

## Goal

Align the project with Now in Android-style boundaries without attempting an unrelated all-at-once rewrite.

## Current Problems

- Stable app models and transport DTOs coexist under `:core:data`.
- Repository contracts expose response DTOs and `ApiResult`.
- Domain code maps DTOs and returns presentation `UiState`.
- Domain code accesses `SecurePreferences`, preference keys, and `NetworkModule`.
- `:core:ui` depends on `:core:data` for rank/style models.
- Features import DTO request types, data-layer UI state, and `ProfileSingleton`.
- Authentication uses mutable module-level access-token state.
- Navigation destinations are centralized in `:core:ui`.

These are existing exceptions. New code must not expand them.

## Target

- `:core:model` owns framework-independent application models.
- `:core:data` repository contracts expose app models and stable results.
- DTO/entity mapping ends inside the data boundary.
- `:core:domain` depends only on repository public APIs and app models.
- `:core:ui` depends on `:core:model` and `:core:designsystem`, not `:core:data`.
- Feature ViewModels own UI state and may use repositories directly for simple operations.
- Session/profile state is repository-backed rather than stored in mutable global objects.
- Features own navigation APIs; `:app` composes the root graph.

## Migration Sequence

### 1. Establish app models

- Add `:core:model` as a Kotlin/JVM module.
- Move one bounded model family at a time, starting with profile and rank types used by both UI and data.
- Keep app models free of Android, serialization, storage, and UI annotations.

### 2. Correct repository boundaries

- Change repository methods to return app models or stable results.
- Move DTO-to-model mapping into repository implementations.
- Mark services, DTOs, mappers, and implementations `internal` where possible.
- Replace one-shot `Flow<ApiResult<ResponseDto>>` APIs with suspending operations or real source-of-truth streams.

### 3. Move UI state to features

- Define feature-specific sealed UI state.
- Map data/domain results in ViewModels.
- Remove `UiState` from `:core:data` after all consumers migrate.

### 4. Remove UI-to-data coupling

- Point `:core:ui` visual mappings at `:core:model`.
- Keep feature-only model-to-visual mapping in the owning feature.
- Remove the `:core:ui -> :core:data` Gradle edge.

### 5. Replace mutable global state

- Replace `ProfileSingleton` with repository-observed or ViewModel-owned state.
- Introduce an injected session/token source.
- Make the network interceptor observe that source instead of static module state.
- Hide preference keys and `SecurePreferences` behind the data boundary.

### 6. Refine UI and navigation modules

- Add `:core:designsystem` and migrate generic primitives from `:core:ui`.
- Move navigation keys and graph registration to features.
- Add feature `api`/`impl` splits only when justified.

### 7. Split infrastructure only when justified

- Add `:core:network`, `:core:datastore`, or `:core:database` when size, reuse, build isolation, or ownership warrants the modules.
- Do not split solely to match NIA's module count.

## Completion Criteria

- No upper-layer imports match the forbidden packages in [Module boundaries](../architecture/module-boundaries.md).
- Repository public contracts expose only stable app models/results.
- Domain contains no UI state, DTO mapper, DataStore, or network-module access.
- `:core:ui` has no `:core:data` dependency.
- Mutable profile and token globals have been removed.
- Focused repository, mapper, UseCase, and ViewModel tests cover migrated behavior.
- Architecture and contract documents describe the implemented state rather than the migration target.

## Non-Goals

- Rewriting all features in one pull request.
- Introducing Room before an offline source of truth is a product requirement.
- Moving repository contracts into domain only to satisfy strict Clean Architecture.
- Creating every module present in Now in Android without a project-specific need.

