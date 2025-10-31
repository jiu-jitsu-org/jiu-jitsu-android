# AGENTS GUIDE

## Repository Snapshot
- **Root Modules:** `:app`, `:core:ui`, `:core:data`, `:core:domain`, `:feature:login`, `:feature:nickname`, `build-logic` composite build.
- **Gradle Setup:** `settings.gradle.kts` enables type-safe project accessors and includes `build-logic`. Convention plugins (`jjs.*`) encapsulate common Android/Kotlin/Hilt configuration.
- **Version Catalog:** `gradle/libs.versions.toml` manages library/plugin coordinates (Kotlin 2.1.10, Compose BOM 2025.09.00, Hilt 2.56.2, Kakao SDK 2.21.7, Credential Manager 1.3.0).

## Key Implementation Details
- `app` module hosts `MainActivity` (`@AndroidEntryPoint`) and Compose navigation root (`AppRoot`, `AppNavHost`). Edge-to-edge behavior and bottom bar visibility depend on destination metadata via `EdgeBehavior` sealed interface.
- `core:ui` supplies the design system: `JiuJitsuPjtTheme`, extensive semantic colors (`ColorComponents`), typography, and reusable components (`PressableButton`, `PrimaryCTAButton`, custom text fields, dialogs, navigation bar).
- `feature:login` implements authentication UI, `LoginViewModel` (Kakao + Google flows using Credential Manager) and `SignUpBottomSheet` with consent state management. TODO stubs exist for Apple login and downstream flows.
- `core:data` / `core:domain` currently contain manifests and placeholders, signaling forthcoming implementation of API/data/use-case layers referenced by ViewModels (`GetBootStrapInfoUseCase`, `CheckAutoLoginUseCase`, etc.).

## Coding & Architecture Conventions
- Compose-first UI with stateless composables and state from Hilt-injected ViewModels.
- Navigation uses type-safe `composable<Route>` APIs, nested graphs, and `popUpTo` policies to keep stacks clean.
- UI state is modeled via `mutableStateOf` with `UiState<T>` wrappers for loading/success/error handling.
- Domain variants modeled through sealed hierarchies (`LoginType`, `SnsLoginSucceedType`, `SignUpAgreeType`, `EdgeBehavior`).
- Compose interactions animated with `animateColorAsState`, `animateFloatAsState`, and click throttling.

## Agent Notes
- Respect existing TODO comments; they indicate planned features (Google Play store intent, Apple login, nickname flow).
- When adding dependencies or plugins, prefer editing convention plugins or version catalog to remain consistent.
- Shared UI components live in `core:ui`; reuse them rather than duplicating styles in feature modules.
- For new features, mirror current module segregation (create `feature:*` or `core:*` additions as appropriate) and wire navigation through `AppNavHost`.

