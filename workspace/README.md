# JiuJitsuPjt

## 개요
JiuJitsuPjt는 Kotlin과 Jetpack Compose로 작성된 multi-module Android application입니다. 프로젝트는 layer 단위(`core`)와 feature 단위(`feature`)로 관심사를 분리하고, `app` module이 navigation과 dependency injection을 조율합니다.

- UI layer: Jetpack Compose + Material 3 + custom design system
- Dependency injection: Hilt (custom Gradle convention plugin)
- Navigation: Jetpack Compose Navigation + type-safe routes
- Build tools: `build-logic`의 custom Gradle conventions, Kotlin 2.1, AGP 8.12

## Module 구조
- `:app` — Application module. `MainActivity`, 전역 navigation(`AppRoot`, `AppNavHost`), Hilt entry point(`App`)를 포함하고 system UI(edge-to-edge), bottom navigation 표시 여부, feature graph 연결을 담당합니다.
- `:core:ui` — Shared design system과 UI components. `Theme.kt`, semantic color map, reusable Compose widgets(button, dialog, text field, navigation bar)을 제공합니다.
- `:core:data` — Data layer placeholder로 manifest와 ProGuard 설정만 포함하며, 추후 network/data source 구현을 수용할 예정입니다.
- `:core:domain` — Domain layer placeholder로 use case와 business logic을 위한 공간입니다.
- `:feature:login` — Authentication flow 담당 feature module. Login screen, agreement bottom sheet, Kakao/Google login을 처리하는 `LoginViewModel`을 제공합니다.
- `:feature:nickname` — Nickname onboarding을 위한 module로 현재 manifest scaffolding만 포함합니다.
- `build-logic` — `includeBuild`로 연결된 composite build. `AndroidApplicationConventionPlugin`, `AndroidLibraryConventionPlugin`, `HiltConventionPlugin` 등 custom Gradle convention plugin이 module별 Kotlin, Compose, Hilt, managed device 설정을 통일합니다.

## Coding Style & Architecture
- **Compose-driven UI:** Screen은 composable function으로 구현되며, 상태는 `ViewModel`에서 전달됩니다. System UI(status/nav bar, inset)는 `LaunchedEffect`와 remember state를 통해 선언적으로 조정합니다.
- **State management:** `ViewModel`은 `mutableStateOf` property를 노출하고, domain flow를 `collectLatest`, `combine`으로 수집해 비동기 데이터를 UI와 연결합니다 (`SplashViewModel`, `LoginViewModel`).
- **Navigation:** Type-safe route(`composable<Route>`)와 nested graph(`navigation<HomeGraph>`)를 활용해 splash, home, login, nickname flow를 분리하고, `popUpTo`, `restoreState`로 back stack을 관리합니다.
- **Design system:** `core:ui`의 `ColorComponents`와 typography wrapper가 component palette를 표준화합니다. `PressableButton`과 같은 reusable UI primitive는 press animation, throttling, styling을 캡슐화합니다.
- **Sealed modeling:** `LoginType`, `SnsLoginSucceedType`, `SignUpAgreeType`, `EdgeBehavior` 등 domain-specific option은 sealed hierarchy로 정의되어 UI 로직에서 완전한 분기 처리를 보장합니다.
- **Gradle conventions:** compileSdk 36, Java/Kotlin target, Compose compiler metrics 등의 공통 설정을 `build-logic`에 집중시켜 중복을 줄이고 module 간 일관성을 유지합니다. Hilt 설정은 custom plugin이 KSP와 dependency, `dagger.hilt.android.plugin` 적용을 자동화합니다.

## Tooling & Dependencies
- Kotlin 2.1.10, Compose BOM 2025.09.00, AndroidX Navigation 2.9.4
- Credential Manager, Kakao SDK, Google Sign-In library (`feature:login`)
- Compose Material 3, Material Icons, Accompanist Permissions, Coil 등 UI 의존성
- Gradle plugin은 version catalog(`libs.versions.toml`)로 관리되며 alias 또는 custom convention ID(`jjs.*`)로 적용됩니다

## 주목할 패턴
- **Edge-to-edge handling:** `MainActivity`와 `EdgeToEdgeChrome`이 현재 destination의 `EdgeBehavior`에 따라 system bar icon 색상과 scrim을 조정합니다.
- **Bottom sheet consent flow:** `SignUpBottomSheet`은 `mutableStateMapOf`로 동의 상태를 추적하며, 필수 동의 여부에 따라 CTA가 “모두 동의” ↔ “다음”으로 전환됩니다.
- **Login orchestration:** `LoginViewModel`은 SNS login 진입, 응답 처리, token 저장을 추상화하며, TODO는 Apple login 지원 등 향후 작업을 표시합니다.

