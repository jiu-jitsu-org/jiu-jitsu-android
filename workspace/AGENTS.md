# AGENTS GUIDE

This file is the project-wide rulebook for coding agents and contributors. Keep it short and operational. Durable product, architecture, decision, contract, development, and operations knowledge belongs in [`docs/`](docs/index.md).

## Start Here

Before changing code:

1. Run `git status --short --branch`.
2. Read [the documentation map](docs/index.md).
3. Read the documents relevant to the task:
   - [Product](docs/product/index.md)
   - [Architecture](docs/architecture/index.md)
   - [Module boundaries](docs/architecture/module-boundaries.md)
   - [Development](docs/development/index.md)
   - [Adaptive Compose UI](docs/development/adaptive-ui.md) for UI work
   - [Definition of done](docs/development/definition-of-done.md)
   - [Active workstreams](docs/workstreams/index.md)
4. Inspect the implementation and tests that own the behavior.

The actual Gradle root is this `workspace/` directory. Current modules are `:app`, `:core:model`, `:core:ui`, `:core:data`, `:core:domain`, `:feature:login`, `:feature:nickname`, and `:feature:profile`; `build-logic` is an included build.

## Non-Negotiable Working Rules

- Preserve unrelated user changes. Never revert, overwrite, or reformat them for convenience.
- Keep changes within the requested scope. Do not turn a feature task into an unrequested architecture rewrite.
- Keep the multi-module structure. Do not move feature code into `:app` or merge modules for convenience.
- Do not add feature-to-feature implementation dependencies.
- Add dependencies through `gradle/libs.versions.toml`; use convention plugins only for genuinely shared configuration.
- Prefer constructor injection and explicit interfaces over service locators, static modules, and mutable globals.
- Keep user-facing text in resources.
- Never print, commit, or document secrets from `local.properties`, Firebase files, signing files, or environment variables.
- Run focused verification in proportion to risk and report commands, results, and limitations.

## Architecture Guardrails

The accepted target is a Now in Android-style architecture, not strict Clean Architecture. See [ADR 0001](docs/architecture/decisions/0001-adopt-nia-style-architecture.md).

- `:core:domain -> :core:data` is allowed.
- Repository contracts remain the public API of `:core:data`.
- Domain and features may use repository contracts and stable app models.
- DTOs, entities, API services, DataStore implementations, Hilt/network modules, repository implementations, and presentation state must not cross the data boundary.
- `:core:ui` must not gain new dependencies on `:core:data`.
- Feature-specific `UiState` belongs in the feature; the small shared async state contract lives in `:core:ui`.
- UseCases are for reusable composition, validation, transformation, or meaningful business operations; do not add pass-through UseCases.
- ViewModels may call repositories directly for simple reads, writes, toggles, and events.
- New shared app models belong in the framework-independent `:core:model`.
- Existing boundary violations are migration targets and must not be used as precedent.

New upper-layer code must not import:

```text
com.kyu.jiu_jitsu.data.api.*
com.kyu.jiu_jitsu.data.model.dto.*
com.kyu.jiu_jitsu.data.datastore.*
com.kyu.jiu_jitsu.data.module.*
com.kyu.jiu_jitsu.data.session.*
com.kyu.jiu_jitsu.data.repository.impl.*
com.kyu.jiu_jitsu.data.model.singleton.*
```

Detailed responsibilities and exceptions are defined in [Module boundaries](docs/architecture/module-boundaries.md) and the active [NIA migration](docs/workstreams/nia-architecture-migration.md).

## Documentation Rules

Update `docs/` in the same change when product behavior, architecture, dependencies, API/storage contracts, verification, release procedures, or workstream status changes.

- Add a new ADR for a new durable architecture decision.
- Do not rewrite accepted ADR history; supersede it with a later ADR.
- Link to canonical source instead of copying full DTOs, Gradle files, or implementation code.
- If code and documentation disagree, investigate and update the stale side.

## Completion

Use [the project definition of done](docs/development/definition-of-done.md). At minimum:

- The requested behavior is complete.
- Relevant code compiles and focused tests pass.
- No forbidden dependency or public-type leak was added.
- Applicable documentation is current.
- Remaining blockers and risks are stated explicitly.

Run `bash scripts/check-architecture.sh` whenever module imports or Gradle dependencies change.
