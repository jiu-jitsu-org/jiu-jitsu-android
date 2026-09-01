# ADR 0001: Adopt a Now in Android-Style Architecture

- Status: Accepted
- Date: 2026-09-01

## Context

The project already uses app, core, and feature modules with Compose, Hilt, repositories, and UseCases. Its boundaries are inconsistent: shared app models, DTOs, UI state, persistence details, and network globals are exposed through `:core:data` to domain and UI code.

The desired reference architecture is Google's Now in Android project and the official Android architecture guidance, not a strict interpretation of Clean Architecture.

## Decision

Adopt an NIA-style layered architecture with:

- A reactive data layer as the source of truth.
- Feature-owned ViewModels and immutable UI state.
- Direct ViewModel-to-repository calls for simple operations and events.
- Domain UseCases for reusable, combined, validated, or meaningful business operations.
- Stable app models in a framework-independent `:core:model`.
- Generic visual primitives in `:core:designsystem`.
- Shared app-specific composites in `:core:ui`.
- App-level composition and root navigation in `:app`.

Adopt boundaries incrementally. Do not copy every NIA module before the project has the size, reuse, build-isolation, or ownership need for it.

## Consequences

Positive:

- The architecture matches the selected Android reference.
- UI, transport, persistence, and app-model concerns become distinguishable.
- Features can use repositories without requiring meaningless pass-through UseCases.
- Shared models become reusable without exposing network or storage details.

Costs:

- Existing DTO, `UiState`, DataStore, route, and singleton dependencies must be migrated.
- New modules and mapping tests add initial work.
- The repository remains Android-oriented until network and persistence modules are separated.

## Alternatives Considered

### Strict Clean Architecture

Repository ports would move into domain-facing modules and data implementations would depend inward. This provides stronger inversion but is not the selected NIA reference and would add more abstraction and migration cost.

### Preserve the current pragmatic layering

This avoids migration work but allows DTO, storage, presentation, and network details to keep spreading across module boundaries.

## Migration

The active sequence is maintained in [the NIA architecture migration workstream](../../workstreams/nia-architecture-migration.md).

