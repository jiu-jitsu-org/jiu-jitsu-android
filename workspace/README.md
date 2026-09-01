# JiuJitsuPjt Workspace

이 디렉터리가 실제 Android Gradle 프로젝트 루트입니다.

## Snapshot

- Project type: Multi-module Android application
- Language: Kotlin
- UI: Jetpack Compose + Material 3
- DI: Hilt + KSP
- Network: Retrofit + OkHttp + Moshi
- Local storage: DataStore + Android Keystore encryption
- Navigation: Navigation Compose type-safe route
- Build: Gradle Kotlin DSL + Version Catalog + included `build-logic`

## Modules

| Module | Responsibility |
| --- | --- |
| `:app` | Application entry point, `MainActivity`, app-level navigation, bottom navigation, Firebase Messaging service |
| `:core:ui` | Shared design system, route definitions, typography, semantic colors, reusable Compose components |
| `:core:data` | Retrofit services, repository implementations, DTOs, model mapping, network module, secure preferences |
| `:core:domain` | UseCases for auth, bootstrap, user profile, community profile, local data |
| `:feature:login` | Kakao/Google login UI and orchestration, signup agreement bottom sheet |
| `:feature:nickname` | Nickname validation, duplication check, signup completion |
| `:feature:profile` | Community profile screen and profile edit flows |
| `build-logic` | Project convention plugins for Android, Compose, Hilt, Firebase setup |

## Runtime Flow

```text
App
└── MainActivity
    └── AppRoot
        ├── AppNavHost
        ├── MainBottomNavigationBar
        └── EdgeToEdgeChrome
```

Primary feature flow:

```text
SplashScreen
→ LoginGraph or HomeGraph
→ LoginScreen
→ NickNameScreen
→ ProfileScreen / Modify* screens
```

Data flow:

```text
Composable Screen
→ Hilt ViewModel
→ UseCase
→ Repository
→ Retrofit service or SecurePreferences
```

## Architecture Notes

- UI state is represented with `UiState<T>` and exposed through Compose state or `StateFlow`.
- API calls are wrapped by `safeApiCall` and mapped to `ApiResult<T>`.
- Navigation routes are defined with `@Serializable` route objects/data classes in `:core:ui`.
- Feature modules depend on `:core:ui` through `jjs.android.feature` and explicitly depend on `:core:data` / `:core:domain` where needed.
- Current dependency direction is pragmatic, not fully clean: `:core:domain` depends on `:core:data`, and `:core:ui` also depends on `:core:data`. If the codebase grows, move repository contracts and domain models out of `:core:data`.

## Architecture Guardrails

Future changes must preserve these project constraints:

1. Keep the current multi-module project structure. Do not collapse `app`, `core`, `feature`, or `build-logic` boundaries for convenience.
2. Keep the `:core:data` and `:core:domain` architecture. `:core:data` owns Retrofit APIs, DTOs, repository implementations, DataStore, network configuration, and data mapping. `:core:domain` owns UseCases and domain-level flow orchestration.
3. Preserve dependency separation inside that structure. Avoid direct feature-to-feature dependencies, keep reusable UI in `:core:ui`, keep data access in `:core:data`, and route business workflows through `:core:domain`.

If architecture cleanup is needed, make the module boundaries and `core:data` / `core:domain` separation stronger rather than weaker.

## Build

```bash
./gradlew :app:assembleDebug --no-daemon
```

Known local build requirement:

- `:app` applies Google Services Plugin.
- `app/google-services.json` or a variant-specific Google services file must exist for `:app:assembleDebug`.

Compile check for core and feature modules:

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

## Configuration Files

- `local.properties`: local-only values such as `BASE_URL`, `DEV_BASE_URL`, Kakao key, Google OAuth client id.
- `app/google-services.json`: Firebase app configuration. Do not commit real production secrets unless repository policy explicitly allows it.
- `gradle/libs.versions.toml`: dependency and plugin versions.
- `build-logic/convention`: convention plugin source.

## Current Implementation Highlights

- `MainActivity` controls edge-to-edge behavior based on the current navigation destination.
- `AppNavHost` owns app-level graph wiring for splash, home, login, nickname, and profile edit flows.
- `LoginViewModel` coordinates Kakao/Google credential acquisition and server login.
- `SecurePreferences` encrypts stored values with Android Keystore AES/GCM before saving them in DataStore.
- `NetworkModule` provides token and non-token Retrofit clients with Hilt qualifiers.
- `ProfileViewModel` fetches and updates community profile information and keeps `ProfileSingleton` in sync.

## Known Gaps From Latest Analysis

- `:app:assembleDebug` is blocked without `google-services.json`.
- Google login button currently sets the login type but does not start the login request from the click handler.
- Apple login is represented in UI/model but not implemented.
- Signup agreement passes marketing consent as `true` regardless of the actual selected agreement list.
- `NetworkModule` timeout constants are `3000L` seconds, which is likely unintended.
- Token handling uses module-level mutable state. A dedicated token provider/interceptor would be safer.
- FCM token is logged and `UpdateAppInfoUseCase` is not yet wired into app startup or token refresh handling.
- Several profile edit screens still contain placeholder button text.
- `toCompetitionRank()` maps only `GOLD`.
- Tests are mostly generated examples; business logic and mapper tests should be added.

## Suggested Test Targets

- `safeApiCall` error mapping.
- DTO to domain mapper behavior, especially nullable API responses.
- `toCompetitionRank()` and other enum mapping functions.
- Nickname validation and duplicate-check state transitions.
- Login success vs new-user branching.
- Profile update request generation.
