# Project Documentation

The `docs/` directory is the project's durable record system. It explains what the product should do, how the system is currently built, why important decisions were made, how work is verified and operated, and which large changes are in progress.

`AGENTS.md` remains the short rulebook for working in the repository. This index is the starting map for understanding the project.

## Documentation Map

| Question | Start here |
| --- | --- |
| What product and user journeys are being built? | [Product](product/index.md) |
| How is the current system structured? | [Architecture](architecture/index.md) |
| What dependencies and public boundaries are allowed? | [Module boundaries](architecture/module-boundaries.md) |
| What are the API and local-storage contracts? | [Contracts](architecture/contracts.md) |
| Why was an architecture choice made? | [Architecture decisions](architecture/decisions/index.md) |
| How should a change be developed and verified? | [Development](development/index.md) |
| How should adaptive Compose UI be handled? | [Adaptive UI](development/adaptive-ui.md) |
| What is the completion standard? | [Definition of done](development/definition-of-done.md) |
| How are builds released and incidents handled? | [Operations](operations/index.md) |
| Which large migrations are active? | [Workstreams](workstreams/index.md) |

## Suggested Reading Paths

For a feature change:

1. Read [Product](product/index.md).
2. Read the relevant feature code and tests.
3. Read [Module boundaries](architecture/module-boundaries.md).
4. Use the [Definition of done](development/definition-of-done.md).

For an architecture or dependency change:

1. Read [Architecture](architecture/index.md).
2. Read [Module boundaries](architecture/module-boundaries.md).
3. Read the applicable [decisions](architecture/decisions/index.md).
4. Check active [workstreams](workstreams/index.md).

For API, authentication, or persistence work:

1. Read [Contracts](architecture/contracts.md).
2. Read [Architecture](architecture/index.md).
3. Read [Operations](operations/index.md) when runtime or release behavior may change.

## Sources of Truth

- `AGENTS.md` defines mandatory working rules for agents and contributors.
- Accepted ADRs in `docs/architecture/decisions/` record architectural intent.
- Current architecture and contract documents describe the intended present state.
- Active workstream documents describe temporary exceptions and migration sequencing.
- Source code and Gradle files are the final executable truth. If code and documentation disagree, investigate the mismatch and update the stale side as part of the task.

Do not silently rewrite an accepted ADR to describe a new decision. Add a new ADR that supersedes it.

## Documentation Update Rules

Update documentation in the same change when any of the following occurs:

- Product behavior, supported journey, or user-visible completion criteria changes.
- A module is added, removed, renamed, or given a different responsibility.
- A dependency direction or public type boundary changes.
- An API endpoint, request/response contract, auth behavior, or persisted key changes.
- Build, test, release, rollback, or incident-response procedures change.
- A large migration begins, changes scope, becomes blocked, or completes.

Prefer concise documents with links to canonical source files. Do not copy full DTOs, Gradle files, or implementation code into documentation.

## Status Vocabulary

- **Current**: implemented and expected to be followed now.
- **Target**: accepted direction that is not fully implemented yet.
- **Active**: work is currently in progress.
- **Proposed**: not yet an accepted decision.
- **Superseded**: replaced by a later decision.

- 공통 WebView: [상세 분석·계획](workstreams/shared-webview.md), [개발 지침](development/webview.md)
