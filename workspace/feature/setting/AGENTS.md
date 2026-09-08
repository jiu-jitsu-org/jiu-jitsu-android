# `:feature:setting` Agent Guide

This guide applies to everything under `feature/setting/`. It extends the parent
[AGENTS.md](../../AGENTS.md). Follow the accepted
[module boundaries](../../docs/architecture/module-boundaries.md) and
[adaptive UI contract](../../docs/development/adaptive-ui.md).

## Current Scope

- This module is a scaffold for the settings page opened from the main bottom navigation.
- `SettingRoute` connects the app destination to an empty `SettingScreen`.
- `SettingViewModel.kt`, `SettingUiState.kt`, and `SettingItem.kt` currently contain package
  declarations and role comments only. They do not implement settings behavior.
- Add concrete menus, state, persistence, and actions only when required by the requested feature.
  Do not infer supported settings or backend capabilities from these placeholder files.

## Ownership and Structure

- `SettingRoute.kt`: obtain the ViewModel when needed, collect state lifecycle-aware, and connect
  screen events to feature operations and app-provided navigation callbacks.
- `screen/SettingScreen.kt`: render supplied state and emit actions; keep it independent of
  ViewModels, repositories, and navigation controllers.
- `viewmodel/SettingViewModel.kt`: coordinate settings reads and writes and expose immutable UI state.
- `model/SettingUiState.kt`: define feature-owned presentation state when actual behavior requires it.
- `components/SettingItem.kt`: hold settings-specific reusable UI that accepts data, callbacks,
  and a Modifier. Do not add screen-level insets or perform data access here.
- `res/values/strings.xml`: own settings-specific user-visible strings.
- Move components to `:core:ui` only after demonstrated cross-feature reuse. Shared stable app
  models belong in `:core:model`; settings presentation state stays in this feature.

## Navigation and App Integration

- The app owns the root NavHost, main Scaffold, bottom navigation, and system-bar appearance.
- Keep the settings entry within `HomeGraph` for its current bottom-tab placement.
- The existing `com.kyu.jiu_jitsu.ui.routes.SettingScreen` object is the navigation destination;
  it is distinct from this module's Screen composable. The app calls `SettingRoute` to connect them.
- The centralized destination is transitional. Do not move it by introducing a dependency from
  `:core:ui` to this feature. Coordinate any future route migration with app-level tab wiring.
- Use callbacks for navigation to login, profile, or other features. Do not add feature-to-feature
  implementation dependencies or duplicate the app's navigation UI in this module.

## State and Data Boundaries

- Use public `:core:data` repository contracts for simple settings reads and writes when needed.
  Introduce a `:core:domain` UseCase only for reusable composition, validation, or meaningful business
  operations; do not add pass-through UseCases.
- Keep DataStore, preference keys, API services, DTOs, session internals, and repository
  implementations behind the data boundary. Inject repository contracts through constructors.
- Treat repository-observed settings as the source of truth. Model pending changes and failures
  explicitly when persistence is introduced; do not display a failed write as successfully saved.
- Add loading, error, retry, and duplicate-action handling in proportion to the implemented behavior.
- Reuse `jjs.android.feature` for shared Gradle configuration. Add only dependencies actually used;
  the scaffold does not require data or domain dependencies.

## Layout

- Apply root `PaddingValues` once in `SettingScreen`, as the scaffold currently does. Do not also
  apply system-bar padding for the same sides or use fixed bottom-navigation compensation.
- Let the app control system-bar appearance; expose semantic intent through callbacks when needed.
- As content is added, keep menus reachable on compact and landscape windows and at large font
  scales. Follow the shared adaptive UI contract for scrolling, expanded widths, and accessibility.

## Verification

Run from the Gradle root (`workspace/`) for code or integration changes:

```bash
./gradlew :feature:setting:compileDebugKotlin --no-daemon
```

- Run `bash scripts/check-architecture.sh` when imports or Gradle dependencies change.
- Run `./gradlew :app:assembleDebug --no-daemon` for app integration changes when configuration
  is available, and verify settings-tab navigation on a device or emulator when behavior changes.
- Add focused state and persistence tests when those behaviors exist. Do not add tests merely
  to mirror comment-only scaffolding.
- Add screenshot coverage when concrete UI is implemented, following the shared adaptive UI guide.
  The scaffold currently has no screenshot plugin or screenshot suite configured.
- For documentation-only changes, check the diff and relative links; a Gradle build is unnecessary.
- Report implemented behavior and performed verification separately. Do not describe the scaffold
  as a completed settings feature.
