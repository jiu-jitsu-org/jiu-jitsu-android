# ADR 0002: Keep Repository Contracts in Core Data

- Status: Accepted
- Date: 2026-09-01

## Context

The project has repository interfaces and implementations in `:core:data`, while `:core:domain` and feature ViewModels consume them. A strict Clean Architecture interpretation would move repository interfaces into domain or a separate ports module.

Now in Android treats repositories as the public API of its data layer. Its domain UseCases depend on those repository contracts, and UI-layer ViewModels may call repositories directly for events and simple operations.

## Decision

Keep repository contracts in `:core:data`.

`:core:domain -> :core:data` and `:feature:* -> :core:data` are allowed when consumers use repository public APIs.

Repository contracts must:

- Expose stable `:core:model` types.
- Hide DTOs, entities, Retrofit responses, DataStore types, and repository implementations.
- Avoid presentation `UiState`.
- Use observable streams for continuing source-of-truth state and suspending results for one-shot work.

Repository implementations and implementation-only mapping should be `internal` where possible.

## Consequences

- Domain does not become the owner of repository interfaces.
- Module dependency direction follows NIA rather than strict dependency inversion.
- Correctness depends on keeping the `:core:data` public surface narrow.
- DTO and infrastructure leakage becomes a boundary violation even though the Gradle dependency itself is allowed.

## Alternative

A future need for stronger compile-time isolation may split `:core:data:api` and `:core:data:impl`. That requires a new ADR because it changes the accepted public-module boundary.

