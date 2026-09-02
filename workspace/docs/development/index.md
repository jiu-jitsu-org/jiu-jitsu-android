# Development

## Before Editing

Run from the repository checkout:

```bash
git status --short --branch
```

Preserve unrelated user changes. Read [the documentation map](../index.md), the applicable product/architecture documents, and any active workstream before changing code.

Run Gradle commands from the actual Gradle root, `workspace/`.

## Configuration

- `local.properties`: local-only values such as backend URLs, Kakao configuration, and Google OAuth client id.
- `app/google-services.json`: Firebase Android configuration.
- `gradle/libs.versions.toml`: dependency and plugin versions.
- `build-logic/convention/`: shared Gradle convention plugins.

Never print, commit, or copy secrets, signing material, production Firebase data, or local configuration into documentation.

## Build and Verification Commands

Full debug app build:

```bash
./gradlew :app:assembleDebug --no-daemon
```

The app build requires `app/google-services.json` or a variant-specific file because the Google Services plugin is applied.

Core and feature compile smoke test:

```bash
./gradlew \
  :core:model:compileKotlin \
  :core:data:compileDebugKotlin \
  :core:domain:compileKotlin \
  :core:ui:compileDebugKotlin \
  :feature:login:compileDebugKotlin \
  :feature:nickname:compileDebugKotlin \
  :feature:profile:compileDebugKotlin \
  --no-daemon
```

Module unit tests:

```bash
./gradlew \
  :core:model:test \
  :core:data:testDebugUnitTest \
  :core:domain:test \
  :core:ui:testDebugUnitTest \
  :feature:login:testDebugUnitTest \
  :feature:nickname:testDebugUnitTest \
  :feature:profile:testDebugUnitTest \
  --no-daemon
```

Architecture boundary scan:

```bash
bash scripts/check-architecture.sh
```

Run the narrowest relevant tests first and broaden verification in proportion to risk. If Firebase configuration blocks the app build, run unaffected module tasks and report the limitation.

Compose screenshot reference generation and validation:

```bash
./gradlew :core:ui:updateDebugScreenshotTest --no-daemon
./gradlew :core:ui:validateDebugScreenshotTest --no-daemon
```

Reference images must be deliberately reviewed before update. See
[Adaptive Compose UI and Screenshot Tests](adaptive-ui.md) for the canonical viewport, font-scale,
IME, determinism, and review contract.

## Gradle Rules

- Add dependencies and plugins through `gradle/libs.versions.toml`.
- Use convention plugins for configuration shared by multiple modules.
- Keep one-off configuration in the consuming module.
- Declare feature-specific data/domain dependencies explicitly.
- Use `api` only when a dependency is intentionally part of a module's public contract.
- Prefer a Kotlin/JVM module for `:core:model`.

## Debug Network Inspection

Debug builds attach the OkHttp Profiler interceptor to the clients constructed by
`core:data`'s `NetworkModule`. Install the **OkHttp Profiler** Android Studio plugin to inspect
those requests. The interceptor is guarded by `BuildConfig.DEBUG` because it writes request and
response headers and bodies to Logcat; it must not run in release builds or be used with shared
production logs.

## Coding Rules

- Prefer constructor injection and explicit interfaces over service locators and static module access.
- Prefer existing shared Compose components before adding duplicates.
- Keep generic UI primitives in `:core:designsystem`, shared app composites in `:core:ui`, and feature-only UI in the feature.
- Keep Route composables responsible for ViewModel state collection and navigation callbacks.
- Keep Screen composables stateless where practical.
- Put user-facing text in resources.
- Follow the [adaptive UI contract](adaptive-ui.md) for edge-to-edge, insets, IME, screen sizing,
  accessibility, and screenshot coverage.
- Name continuing reads with `observe...` when they return a stream.
- Do not broaden a feature task into an unrelated full architecture migration.

## Testing Priorities

Add focused tests for behavior changed by the task, especially:

- DTO/entity-to-app-model mapping and nullable API fields.
- Enum mapping, including unknown or newly supported backend values.
- Repository success and failure behavior.
- UseCase stream combination and validation.
- Login existing-user vs new-user branching.
- Nickname validation and duplication-check transitions.
- Profile update command generation.
- ViewModel loading, content, error, and retry state transitions.
- Token refresh and request retry when authentication behavior changes.

Use [the definition of done](definition-of-done.md) before handing off work.
