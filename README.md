# 🥋 JIU JITSU Project


</br>

## :eyes: 환경 세팅

- Android Studio Narwhal Feature Drop | 2025.1.2 Patch 2 </br>
![alt](./img/img_as.png "Android Studio Narwhal Feature Drop")</br></br>

- AGP : 8.12.2</br>

</br>

## 🌿 브랜치 전략 (GitHub Flow)

```bash
main (프로덕션)
├── develop (개발 통합)
├── feature/user-auth (기능 개발)
├── feature/video-upload
├── hotfix/critical-bug (긴급 수정)
└── release/v1.0.0 (릴리스 준비)
```

## 💬 커밋 컨벤션 (Conventional Commits)

```bash
형식: <type>(scope): <description>

feat(auth): 소셜 로그인 구현
fix(video): 업로드 실패 이슈 해결
docs(readme): 개발 환경 설정 가이드 추가
style(ui): 메인 화면 레이아웃 개선
refactor(api): 사용자 API 구조 개선
test(unit): 로그인 기능 테스트 케이스 추가
chore(deps): iOS 라이브러리 업데이트
```

## 🗂️ `workspace/`

### ⚫️ 요약 

**Jetpack Compose + Hilt + Retrofit + DataStore + 멀티모듈 아키텍처**를 중심으로 구성.  
`core` 와 `feature` 분리를 통하여 기능 확장과 유지보수를 고려하여 구조 설계.

### ⚫️ 프로젝트 구조

`app + core + feature + build-logic` 구성된 멀티모듈 프로젝트입니다.  
구조상 `공통 기능(core)` 과 `화면 단위 기능(feature)` 를 분리해 확장성과 유지보수성을 고려하여 구성했습니다.

```bash
workspace/
├── app/                 # 앱 진입점, Application/MainActivity, 전체 Navigation
├── build-logic/         # 공통 Gradle Convention Plugin 관리
├── core/
│   ├── data/            # API, Repository 구현, Network/DataStore/Hilt 모듈
│   ├── domain/          # UseCase 계층
│   └── ui/              # 공통 UI 컴포넌트, Theme, Route 정의
├── feature/
│   ├── login/           # 로그인 화면, SNS 로그인 로직
│   ├── nickname/        # 닉네임 입력/검증 플로우
│   └── profile/         # 프로필 조회/수정, 스타일/대회/소속 수정
├── gradle/
│   └── libs.versions.toml   # 버전 카탈로그
└── settings.gradle.kts      # 멀티모듈 구성 정의
```

### ⚫️ 모듈별 역할

| 모듈 | 역할 |
|---|---|
| `app` | 앱 시작점. `@HiltAndroidApp`, `MainActivity`, `AppNavHost` 를 통해 전체 화면 흐름과 바텀 네비게이션 관리 |
| `core:data` | Retrofit/OkHttp/Moshi 기반 네트워크 계층, Repository 구현체, DataStore 기반 로컬 저장소 제공 |
| `core:domain` | UseCase 중심의 도메인 계층. 화면 로직이 데이터 계층에 직접 의존하지 않도록 중간 계층 역할 수행 |
| `core:ui` | 공통 Theme, Button/TextField/Dialog/Card/Picker 등 재사용 UI 컴포넌트 제공 |
| `feature:login` | 카카오/구글 로그인, 회원가입 바텀시트 등 인증 진입 기능 담당 |
| `feature:nickname` | 닉네임 입력 및 검증 화면 담당 |
| `feature:profile` | 프로필 조회/수정, 아카데미/대회/스타일 수정 등 마이페이지 성격의 기능 담당 |
| `build-logic` | 공통 Android/Compose/Hilt/Firebase Gradle 설정을 Convention Plugin 으로 관리 |

### ⚫️  아키텍처 흐름

```text
UI(Compose Screen)
→ ViewModel(Hilt 주입)
→ UseCase(core:domain)
→ Repository(core:data)
→ Retrofit / DataStore
```

화면은 `ViewModel` 을 통해 상태를 다루고, 실제 비즈니스 로직은 `UseCase`, 데이터 접근은 `Repository` 가 담당하는 계층형 구조입니다.  
테스트 용이성, 책임 분리, 기능별 모듈 독립성 측면을 고려하여 구성했습니다.

### ⚫️ 사용된 Android 개발 스킬

####  프로젝트/빌드 구성

- `Gradle Kotlin DSL` 기반 빌드 스크립트 사용
- `Version Catalog(libs.versions.toml)` 로 라이브러리 버전 일원화
- `build-logic` 포함 빌드로 `Convention Plugin` 직접 관리
- `Typesafe Project Accessors` 활성화
- 멀티모듈 구조(`app`, `core`, `feature`) 설계

####  UI 개발

- `Jetpack Compose` 기반 UI 구성
- `Material 3` 사용
- 공통 버튼, 다이얼로그, 카드, 피커 등 `재사용 UI 컴포넌트` 설계
- `Edge-to-Edge UI`, `WindowInsets`, 애니메이션(`AnimatedVisibility`) 적용
- `Navigation Compose` + 타입 세이프 Route(`toRoute`, `@Serializable`) 사용

####  상태관리/아키텍처

- `MVVM` 패턴 적용
- `ViewModel` 기반 화면 상태 관리
- `UseCase` 중심 Domain 계층 분리
- `Repository Pattern` 적용
- `UiState`, `ApiResult` 형태로 비동기 결과/에러 상태 추상화

#### 네트워크/데이터

- `Retrofit` 기반 API 통신
- `OkHttp` 인터셉터 및 프로파일러 사용
- `Moshi` 기반 JSON 파싱
- `DataStore` 기반 로컬 데이터 저장
- `BuildConfig` + `local.properties` 로 환경별 값 주입

#### 의존성 주입 및 외부 연동

- `Hilt` 기반 DI 구성
- `@Module`, `@Provides`, `@HiltViewModel`, `@AndroidEntryPoint` 사용
- `Kakao SDK` 연동
- `Android Credentials API` + `Google ID` 기반 구글 로그인 연동
- `Firebase`, `Secrets Gradle Plugin` 도입 흔적 존재


