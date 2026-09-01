# Definition of Done

A change is complete when every applicable item below is satisfied.

## Scope and Behavior

- The requested behavior and acceptance criteria are implemented.
- Loading, empty, content, error, retry, and offline behavior are handled where applicable.
- No unrelated code or user-owned change was reverted.
- Known limitations are explicitly reported.

## Architecture

- No new feature-to-feature implementation dependency was added.
- Upper layers do not newly import DTOs, API services, response envelopes, DataStore implementations, `NetworkModule`, repository implementations, or mutable data singletons.
- Repository public APIs expose stable app models or stable results.
- DTO/entity mapping occurs inside the data boundary.
- Feature UI state remains in the feature.
- New shared models are framework independent and placed in `:core:model`.
- Changed Gradle dependencies are the narrowest necessary edges.

## UI and Accessibility

- Screen composables are stateless where practical.
- User-facing strings use resources.
- Relevant loading, disabled, error, and retry states are visible and usable.
- Content descriptions, semantics, focus, and touch targets are considered for changed UI.
- Relevant Compose previews or screenshot coverage are updated when available.

## Verification

- Changed code compiles.
- Focused unit tests pass.
- Broader module tests are run when risk warrants them.
- The app build is run when Firebase configuration is available.
- Manual verification is performed for behavior that automated tests do not cover.
- Secrets or sensitive values do not appear in source, logs, diffs, or documentation.

## Documentation

- Product docs are updated for changed user-visible requirements.
- Architecture docs and ADRs are updated for changed boundaries or decisions.
- Contract docs are updated for changed endpoints, payload families, auth rules, or persisted keys.
- Operations docs are updated for changed build, release, rollback, or incident procedures.
- Active workstream status is updated when a migration milestone changes.

## Handoff

- The final summary states what changed.
- Verification commands and outcomes are reported.
- Remaining risks, blockers, migrations, or follow-up work are identified without presenting incomplete work as complete.

