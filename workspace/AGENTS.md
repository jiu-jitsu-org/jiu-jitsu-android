# AGENTS GUIDE

This file summarizes the current project shape and the practical rules for future coding agents working in this Android workspace.

## Workspace

- Actual Gradle root: `workspace/`
- Current module set: `:app`, `:core:ui`, `:core:data`, `:core:domain`, `:feature:login`, `:feature:nickname`, `:feature:profile`
- Included build: `build-logic`
- Version catalog: `gradle/libs.versions.toml`
- Convention plugin ids use the `jjs.*` prefix.

Always check `git status --short --branch` before editing. This repository may contain user-owned in-progress changes; do not revert unrelated modifications.

## Build Commands

Full app build:

```bash
./gradlew :app:assembleDebug --no-daemon
```

Known blocker:

- `:app` applies `com.google.gms.google-services`.
- Full app build requires `app/google-services.json` or a variant-specific Google services file.

Module compile smoke test:

```bash
./gradlew \
  :core:data:compileDebugKotlin \
  :core:domain:compileDebugKotlin \
  :core:ui:compileDebugKotlin \
  :feature:login:compileDebugKotlin \
  :feature:nickname:compileDebugKotlin \
  :feature:profile:compileDebugKotlin \
  --no-daemon
```

## Architecture

Runtime flow:

```text
Compose Screen
→ Hilt ViewModel
→ UseCase
→ Repository
→ Retrofit service or SecurePreferences
```

Important packages:

- `app/src/main/java/com/kyu/jiu_jitsu`: `App`, `MainActivity`, navigation root, splash flow, Firebase service
- `core/ui/src/main/java/com/kyu/jiu_jitsu/ui`: theme, reusable Compose components, route definitions
- `core/data/src/main/java/com/kyu/jiu_jitsu/data`: API, DTO, model, repository, DataStore, network/Hilt modules
- `core/domain/src/main/java/com/kyu/jiu_jitsu/domain`: UseCases and domain helpers
- `feature/login`: login UI and SNS login orchestration
- `feature/nickname`: nickname validation and signup completion
- `feature/profile`: profile display/edit flows

Current dependency direction is pragmatic rather than strictly clean. `:core:domain` and `:core:ui` depend on `:core:data`; avoid deepening that coupling unless the task explicitly requires it. For larger refactors, prefer moving repository contracts and stable domain models into `:core:domain`.

## Required Architecture Conditions

These conditions are project rules, not optional suggestions:

1. Keep the current multi-module project structure. Do not merge modules or move feature code into `:app` for convenience.
2. Keep the `:core:data` and `:core:domain` architecture. `:core:data` owns data sources, DTOs, repository implementations, network setup, local storage, and data mapping. `:core:domain` owns UseCases and domain-level orchestration.
3. Preserve dependency separation while keeping the above structure. Do not add direct feature-to-feature dependencies. Do not move API/DataStore/network code into feature modules or `:core:domain`. Do not put business workflow logic in `:core:data` when it belongs in a UseCase.

When changing dependencies, prefer the narrowest module edge that satisfies the feature. If a new shared capability is needed, place it in the appropriate `core` module rather than coupling two feature modules.

## Coding Conventions

- Prefer existing Compose components from `:core:ui`.
- Add shared visual primitives to `:core:ui` instead of duplicating them in feature modules.
- Add dependencies through `gradle/libs.versions.toml`; use convention plugins when a dependency or plugin applies across modules.
- For feature modules, follow the existing `feature/*` structure: `screen`, `components`, `model`, `viewmodel` where applicable.
- Keep route definitions centralized in `core/ui/.../routes/AppRoutes.kt`.
- Keep network endpoint constants in `NetworkConfig`.
- For API calls, follow the existing `safeApiCall` -> `ApiResult` -> `UiState` mapping pattern.

## Known Implementation Risks

- Full app build fails without Firebase `google-services.json`.
- Google and Apple login UI paths need completion.
- Signup marketing consent handling should use the selected agreement list, not a hardcoded value.
- `NetworkModule` timeout constants are currently very large because they are passed as seconds.
- Auth token handling uses module-level mutable state; a token provider/interceptor is preferable.
- FCM token logging should be removed before release.
- `UpdateAppInfoUseCase` exists but is not wired into app startup or FCM token refresh.
- Several profile edit screens still use placeholder button text.
- Competition rank mapper currently handles only `GOLD`.
- Unit and instrumented tests are mostly generated examples.

## When Editing

- Do not commit or print secret values from `local.properties` or Firebase configuration files.
- Do not add production credentials to the repository.
- If adding Firebase functionality, preserve local build behavior for developers without real `google-services.json`.
- If changing login or token behavior, verify both existing-user sign-in and new-user signup paths.
- If changing profile models or mappers, add tests for nullable API responses and enum mapping.
- If touching UI text, replace placeholder strings with resources where the text is user-facing.

## Recommended Next Fixes

1. Restore app build by providing Firebase config locally or applying Google Services conditionally.
2. Wire Google button click to `startSnsLogin(context)` and hide/remove the Kakao test button outside debug.
3. Pass actual marketing consent from `SignUpBottomSheet`.
4. Fix network timeout units and replace global token state with an injected token source.
5. Wire FCM token update into `UpdateAppInfoUseCase`.
6. Replace profile edit placeholder button labels and complete competition rank mapping.
7. Add focused unit tests for use cases, mappers, and nickname/login state transitions.
