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
├── app/                 # 앱 진입, root navigation, chrome, 네이티브 로그인 조합
├── build-logic/         # 공통 Gradle convention
├── core/
│   ├── model/           # Kotlin/JVM 앱 모델
│   ├── data/            # Repository 계약/구현, API, 암호화 세션, 공통 갱신, 웹 쿠키
│   ├── domain/          # 재사용 검증/비즈니스 변환
│   ├── ui/              # 공통 Compose UI/디자인 시스템
│   └── webview/         # 공통 WebView 런타임, URL 정책, 브리지/문서 수명
├── feature/
│   ├── login/           # 소셜 로그인
│   ├── nickname/        # 닉네임/가입
│   ├── profile/         # 프로필
│   └── web/             # 웹 화면 스택, 인증 조합, dialog/sheet/chooser/share
└── docs/                # 구조, 계약, 개발 지침, 작업 계획
```

현재 구조는 **Now in Android 방식**을 따른다. Repository 계약은 `:core:data`에 있고,
상위 계층은 안정된 앱 모델과 Repository API를 사용한다. 단순 읽기/쓰기에 pass-through
UseCase를 추가하지 않는다. `:core:ui`와 `:core:webview`는 data에 의존하지 않는다.

정본은 [문서 지도](workspace/docs/index.md), [모듈 경계](workspace/docs/architecture/module-boundaries.md),
[공통 웹뷰 분석·계획](workspace/docs/workstreams/shared-webview.md),
[웹뷰 개발 지침](workspace/docs/development/webview.md)이다.

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

앱 빌드에는 Firebase 설정이 필요하다. `app/google-services.json` 또는 variant별 파일을 사용한다.
웹뷰 검증 명령과 현재 결과는 [웹뷰 작업 계획](workspace/docs/workstreams/shared-webview.md)에 기록한다.

```bash
cd workspace
./gradlew :core:webview:testDebugUnitTest :core:data:testDebugUnitTest :feature:web:compileDebugKotlin --no-daemon
bash scripts/check-architecture.sh
```

미완료 제품 기능과 웹 공동 검수 조건은 [제품 문서](workspace/docs/product/index.md)를 참조한다.

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
