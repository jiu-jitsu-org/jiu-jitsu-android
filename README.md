# JIU JITSU Android

JiuJitsu Android 프로젝트는 Kotlin, Jetpack Compose, Hilt, Retrofit, DataStore 기반의 멀티모듈 Android 애플리케이션입니다.

실제 Gradle 프로젝트 루트는 `workspace/`입니다. 최상위 디렉터리는 프로젝트 문서와 Android workspace를 감싸는 저장소 루트 역할을 합니다.

## 개발 환경

- Android Studio: Narwhal Feature Drop 2025.1.2 Patch 2 기준
- Android Gradle Plugin: 8.12.2
- Gradle Wrapper: 8.13
- Kotlin: 2.1.10
- Java target: 17
- compileSdk / targetSdk: 36
- minSdk: 26

## 프로젝트 구조

```text
workspace/
├── app/                 # Application, MainActivity, 전역 Navigation, Firebase Messaging
├── build-logic/         # 공통 Gradle Convention Plugin
├── core/
│   ├── data/            # Retrofit API, Repository 구현, DataStore, Network/Hilt 모듈
│   ├── domain/          # UseCase 계층
│   └── ui/              # Theme, Route, 공통 Compose 컴포넌트
├── feature/
│   ├── login/           # Kakao/Google 로그인, 회원가입 동의 플로우
│   ├── nickname/        # 닉네임 입력, 검증, 가입 완료 플로우
│   └── profile/         # 커뮤니티 프로필 조회/수정, 스타일/대회/도장 정보
├── gradle/
│   └── libs.versions.toml
└── settings.gradle.kts
```

## 모듈 역할

| 모듈 | 역할 |
| --- | --- |
| `:app` | 앱 진입점, `@HiltAndroidApp`, `MainActivity`, `AppNavHost`, bottom navigation, Firebase Messaging 서비스 |
| `:core:data` | Retrofit/Moshi API, OkHttp client, Repository 구현체, Secure DataStore, DTO/model mapping |
| `:core:domain` | 로그인, 부트스트랩, 유저, 커뮤니티 프로필, 로컬 데이터 관련 UseCase |
| `:core:ui` | 디자인 시스템, semantic color, typography, 공통 button/text field/dialog/picker/card/navigation component |
| `:feature:login` | Kakao/Google SNS 로그인, 신규 유저 약관 동의 bottom sheet |
| `:feature:nickname` | 닉네임 유효성 검사, 중복 확인, 회원가입 처리 |
| `:feature:profile` | 프로필 조회/수정, 벨트/체급, 도장명, 대회 이력, 주짓수 스타일 등록 |
| `build-logic` | `jjs.*` convention plugin으로 Android/Compose/Hilt 설정 공통화 |

## 아키텍처 흐름

```text
Compose Screen
→ Hilt ViewModel
→ UseCase (:core:domain)
→ Repository interface / implementation (:core:data)
→ Retrofit API or Secure DataStore
```

현재 구현은 실용적인 계층 분리를 따릅니다. 다만 `:core:domain`과 `:core:ui`가 `:core:data`에 직접 의존하고 있어, 엄격한 Clean Architecture라기보다는 빠른 기능 개발에 맞춘 구조입니다. 장기적으로는 domain model과 repository contract를 `:core:domain`으로 옮기면 테스트와 모듈 독립성이 좋아집니다.

## 아키텍처 유지 조건

향후 기능 추가, 버그 수정, 리팩터링 작업에서는 아래 조건을 우선합니다.

1. 현재의 멀티모듈 프로젝트 구성을 유지합니다. 기능 추가 시 `app`, `core`, `feature`, `build-logic`의 역할을 유지하고, 편의를 위해 모듈을 합치지 않습니다.
2. `:core:data`와 `:core:domain` 계층 구조를 유지합니다. API, DTO, Repository 구현, DataStore, Network 설정은 `:core:data`에 두고, UseCase와 도메인 흐름 조합은 `:core:domain`에 둡니다.
3. 위 구조 안에서 의존성 분리를 지킵니다. feature 간 직접 의존을 만들지 않고, 공통 UI는 `:core:ui`, 데이터 접근은 `:core:data`, 비즈니스 흐름은 `:core:domain`을 통해 다룹니다.

구조 개선이 필요하더라도 멀티모듈과 `core:data` / `core:domain` 분리를 깨지 않는 방향으로 진행합니다.

## 주요 기술

- UI: Jetpack Compose, Material 3, Navigation Compose, custom design system
- DI: Hilt, KSP
- Network: Retrofit, OkHttp, Moshi
- Local storage: DataStore + Android Keystore 기반 암호화
- Auth: Kakao SDK, Android Credentials API, Google ID
- Push: Firebase Messaging 도입 중
- Build: Gradle Kotlin DSL, Version Catalog, included build `build-logic`, type-safe project accessors

## 빌드 및 검증

```bash
cd workspace
./gradlew :app:assembleDebug --no-daemon
```

현재 로컬 분석 기준으로 `:app:assembleDebug`는 `app/google-services.json` 부재로 실패합니다. Firebase Google Services Plugin을 사용하므로 로컬 개발에는 다음 중 하나가 필요합니다.

- `workspace/app/google-services.json` 또는 variant별 `workspace/app/src/debug/google-services.json` 배치
- Firebase가 필요 없는 로컬 빌드에서 Google Services Plugin 조건부 적용

Firebase 설정 파일과 별개로 아래 core/feature Kotlin 컴파일은 성공했습니다.

```bash
cd workspace
./gradlew \
  :core:data:compileDebugKotlin \
  :core:domain:compileDebugKotlin \
  :core:ui:compileDebugKotlin \
  :feature:login:compileDebugKotlin \
  :feature:nickname:compileDebugKotlin \
  :feature:profile:compileDebugKotlin \
  --no-daemon
```

## 현재 분석 기준 주의 사항

- Google/Apple 로그인 버튼은 UI가 있으나 실제 로그인 시작 연결이 일부 미완성입니다.
- 신규 가입 약관 플로우에서 마케팅 동의값 전달 로직을 확인해야 합니다.
- `NetworkModule`의 timeout 값과 token 관리 방식은 정리가 필요합니다.
- FCM 토큰 로그 출력은 릴리스 전에 제거해야 합니다.
- 프로필/대회/스타일 수정 화면에는 placeholder 텍스트와 TODO가 남아 있습니다.
- 테스트는 대부분 Android Studio 기본 예제 수준이라 핵심 UseCase, mapper, ViewModel 테스트 보강이 필요합니다.

## 브랜치 전략

GitHub Flow를 기본으로 사용합니다.

```text
main
├── develop
├── feature/*
├── hotfix/*
└── release/*
```

## 커밋 컨벤션

Conventional Commits 형식을 사용합니다.

```text
<type>(scope): <description>

feat(auth): 소셜 로그인 구현
fix(profile): 대회 결과 매핑 오류 수정
docs(readme): 프로젝트 구조 문서 최신화
refactor(network): 토큰 인터셉터 구조 개선
test(nickname): 닉네임 검증 테스트 추가
chore(deps): Firebase Messaging 의존성 추가
```
