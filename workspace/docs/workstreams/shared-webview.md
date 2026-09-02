# 공통 WebView 도입: 분석과 작업 계획

- 상태: Active — Android 구현 완료, 개발 서버 기초 검수 완료, 공동 출시 검수 잔여
- 분석일: 2026-09-02
- 입력: [TO_ANDROID_DEV.md](../../TO_ANDROID_DEV.md), 웹 develop `78570b162a06`
- 입력 파일은 저장소 최상위가 아닌 **Gradle 루트 workspace/**에 있다. 원문은 수정하지 않는다.
- 웹 소스는 이 Android 저장소에 없으므로 원문의 웹 소스 분석을 인수 계약으로 사용한다.
  아래의 “웹 현재 동작”은 웹 코드를 직접 재검증했다는 의미가 아니다.

## 기존 프로젝트 조사

| 항목 | 확인 결과 | 적용 판단 |
| --- | --- | --- |
| 구조 | app, core:model/data/domain/ui, feature:login/nickname/profile | core:webview와 feature:web 추가 |
| 아키텍처 | ADR 0001/0002의 NIA, repository 계약은 data에 유지 | strict Clean Architecture로 재편하지 않음 |
| 홈 | RedScreen/GrayScreen placeholder, ProfileScreen | 기존 첫 홈 탭에 웹 피드 연결 |
| 인증 | SessionRepository, 암호화 SessionLocalDataSource, AccessTokenProvider | 기존 durable session 재사용, 가입 임시 토큰 제외 |
| 갱신 | REST TokenRefreshInterceptor 내부 lock/HTTP | WebView와 REST가 공유하는 refresh coordinator로 추출 |
| 로그인 | Kakao 로그인/닉네임 가입, Google/Apple 미완성 | 기존 로그인 UI를 app에서 조합, 취소/skip은 CANCELLED |
| 빌드 | Kotlin 2.1.10, AGP 8.12.2, Java 17, min 26/target 36 | 기존 convention/catalog 재사용 |
| inset | app Scaffold/chrome, 화면별 padding | 서브/로그인은 하단 탭 숨김, 웹 영역에서 padding/IME 적용 |
| 문서 | 최상위 README의 구조/UseCase/빌드 설명 일부 오래됨 | 정본 docs로 연결하고 현재 구조로 수정 |
| 환경 | debug Firebase 파일 존재, 사용자 제공 개발/운영 웹 origin 확보 | 설정 주입, 누락 상태 UI, 개발 health/상세 HTTP200 확인, 앱 검수 별도 |

## 원문 절별 상세 분석

| 절 | 계약·의미 | Android 작업·검증 |
| --- | --- | --- |
| 1 필수 계약 | 모든 WebView에 동일 AppBridge, READY 후 회신, refresh는 앱, 상세는 별도 화면 | 공통 생성기/문서 상태/스택으로 강제 |
| 2 주소 | WEB_ORIGIN은 Next 서버, API 서버가 아님. basePath 없음. SSR/BFF는 APK asset에서 실행 불가 | 독립 origin 설정, HTTPS/정규화 port/userinfo 검사, 초기 loadUrl도 검사 |
| 2 경로 | 피드는 /, 상세 /community/{positive id}, 작성 /community/write, policies/service/version | /community와 /edit 및 /api 화면 차단. 정책에는 네이티브 닫기 |
| 2 로컬 | adb reverse localhost 권장. IP HTTP는 randomUUID secure context 문제 | HTTP debug allowlist를 좁게 유지, production Secure 쿠키 약화 금지 |
| 3 책임 | SSR 첫 조회와 브라우저 BFF 요청은 다름. 변경 API/업로드는 웹 책임 | 네이티브는 shouldInterceptRequest로 웹 네트워크를 재구현하지 않음 |
| 4 초기화 | JS/DOM storage 필수, third-party cookie 불필요, file/mixed content 금지 | 모든 메인/서브에 동일 설정·R8 규칙 |
| 4 transport | JS interface는 background thread, WebBridge.receive는 웹 소유 | main으로 dispatch, JSON 이중 escape, 문서 origin/수명 확인 |
| 4 수명 | READY 전 guard/refresh 도착 가능. onPageFinished는 READY가 아님 | generation 증가, 전송 queue, 반복 READY 무해, stale callback 폐기 |
| 4 URL | startsWith는 위장 host/port에 취약. 정상 same-origin 이동은 false 반환 | URI parser, 외부 HTTPS 브라우저 분리, 리다이렉트에서도 차단 |
| 5 프로토콜 | 11 web→app / 7 app→web, requestId는 payload 내부, 범용 ACK/version 없음 | typed decode와 필수 타입 검증, 알 수 없는 type 안전 무시 |
| 6 세션 | oss_session은 accessToken 자체, HttpOnly/Lax/host-only/path /, 7일 | BFF 응답 확인 후 Set-Cookie 속성 그대로 CookieManager 반영 |
| 6 인증 의미 | session GET authenticated=true는 쿠키 존재만 확인 | 토큰 유효성 검증으로 쓰지 않음. 보호 API에서 만료 복구 |
| 6 첫 SSR | READY 전 SSR은 이미 실행됨 | 첫 load 전에 POST/쿠키 callback, 익명은 기존 쿠키 만료 |
| 6 로그인 | PROMPT 안내와 MODAL 직접 로그인 구분, 거절/back/skip 취소 | 단일 로그인 흐름, 원본 문서에 SUCCESS/CANCELLED, 화면 유지 |
| 6 만료 | 웹 자동 조건은 HTTP403 + details.code A0003, 15초, 원 요청 1회 | 네이티브 HTTP 갱신 8초 한도, 중복 refresh 병합, 모든 대기 문서에 회신 |
| 6 SSR 복구 | 웹 router.refresh, 동일 경로 30초 cooldown | Android 임의 reload나 반복 refresh를 추가하지 않음 |
| 6 logout/계정 | 늦은 refresh/login이 이전 사용자를 되살릴 수 있음 | 인증 revision 검사, 토큰 업데이트 원자성, 전환 시 WebView 폐기/쿠키 교체 |
| 7 스택 | 목록 React/스크롤 보존, CLOSE는 요청한 최상단 서브만 | 루트 CLOSE 무시, 다른 문서 pop 금지, 부모 GONE/resume |
| 7 back | IME/표면 우선, guard면 BACK_PRESSED만, 웹 CLOSE 대기 | back 직렬화, 무가드 서브 pop, 루트 history 정책 |
| 7 confirm | confirm/cancel/dismiss 구분, outside 옵션, destructive | native UI, 결과 한 번, 5분 후 dismiss 정리 |
| 7 select | 동적 options, 선택 전 submit 비활성, customText 선택 사항 | value 보존, 기타 입력 선택 사항, 미지원/중복 요청 정리 |
| 7 식별 | requestId는 문서 안에서만 유일 | webViewId + generation + requestId, 완료 뒤 중복 차단 |
| 8 이미지 | input file image/*, 최대 3개는 웹 규칙, multiple 여부 반영 | Activity Result chooser, content URI 읽기/이미지/자기 앱 provider 검사 |
| 8 업로드 | 웹 압축→서명→ImageKit→이미지 등록→게시글 생성 | 앱 중복 업로드/Base64/등록 재시도 금지 |
| 8 공유 | navigator.share 후 fallback SHOW_SHARE_SHEET, 응답 없음 | 검증 URL ACTION_SEND/chooser, 환경 주소 유지 |
| 9 viewport | 웹 visualViewport + safe area, native inset 중복 위험 | web host 한 곳에서 padding/IME, 회전·한글·TalkBack 검수 |
| 9 복귀 | dirty posts sessionStorage, created TTL30초, toast TTL10초, DOM visibility 의존 | 부모 보존. onResume가 이벤트 보장 아님. 실제 검수/공동 복귀 계약 필요 |
| 9 오류/해제 | 이미지404와 main-frame 오류 구별, READY 미수신 복구, pauseTimers 전역 영향 | main 오류 UI, timeout/retry, callback 정리, destroy, pauseTimers 미사용 |
| 10 보안 | 토큰 URL/storage/log 금지, bridge는 모든 frame 노출 | 신뢰 웹/CSP 필요, HTTPS/origin/세대 재검사, SSL cancel |
| 11 한계 | 웹 오류와 앱 미구현을 구별해야 함 | 아래 공동 backlog로 분리하고 출시 전 확인 |
| 12 검수 | 문서 체크박스는 실행 결과가 아님 | 자동 검증과 배포/실기기 미검증을 분리 기록 |
| 13 구조 | factory/transport/router/auth/navigation/UI 분리 제안 | 기존 NIA 모듈 경계에 맞춰 ADR 0003으로 확정 |
| 14 근거 | messages.ts, auth-provider, BFF, use-feed-revalidate 등 | 변경 시 웹 SHA와 인수 문서 갱신 요구 |

## 메시지 구현 매핑

| 웹 요청 | 처리와 앱 응답 |
| --- | --- |
| WEBVIEW_READY | 문서당 1회 준비, AUTH_LOGIN_SUCCESS 또는 AUTH_LOGOUT |
| AUTH_LOGIN_PROMPT | 안내 후 로그인 slot, SUCCESS/CANCELLED |
| AUTH_LOGIN_MODAL | 로그인 slot 바로 열기, SUCCESS/CANCELLED |
| AUTH_LOGOUT_REQUEST | 앱 세션 정리·전체 웹 수명 정리·익명 쿠키 준비 |
| AUTH_TOKEN_REFRESH_REQUEST | 공통 갱신, AUTH_LOGIN_SUCCESS/AUTH_SESSION_EXPIRED |
| OPEN_SUBVIEW | origin/path 검증 후 독립 WebView push/modal |
| CLOSE_SUBVIEW | 발신 최상단 서브만 닫기 |
| BACK_GUARD | 해당 문서 guard 업데이트, 앱 back에 BACK_PRESSED |
| SHOW_CONFIRM_DIALOG | CONFIRM_DIALOG_RESULT, requestId 보존 |
| SHOW_SELECT_SHEET | SELECT_SHEET_RESULT, value/customText 보존 |
| SHOW_SHARE_SHEET | Android Sharesheet, 응답 없음 |

## 작업 순서와 완료 기준

1. **[완료] 문서/구조 선행**: 분석, ADR, 개발 지침, 모듈별 AGENTS와 정본 링크.
2. **[완료] 공통 런타임**: URL 정책, 타입 검증, READY queue, generation, WebView 설정/해제.
3. **[완료] 인증**: REST/웹 refresh 공유, stale token 쓰기 차단, BFF/cookie 준비와 익명 정리.
4. **[완료] 화면**: 보존 스택, back, dialog/sheet, chooser/share, loading/error/retry.
5. **[완료] 앱 조합**: 기존 홈 첫 탭, 로그인/가입 slot, fullscreen chrome/insets.
6. **[완료] 자동 검증**: 정책/프로토콜/세대/동시 refresh 테스트, 경계 검사, debug 빌드/release 컴파일.
7. **[일부 완료] 공동 인수**: 실제 origin과 테스트 계정 확보 후 원문 §12.2 기기 검수.

## 공동 backlog와 출시 조건

| 담당 | 미해결 항목 | 완료 근거 |
| --- | --- | --- |
| 배포 | origin 확보 완료. 배포 SHA, 신뢰 HTTPS, CSP frame 제한 | 실제 주소·배포 설정 검수 |
| 웹 | GET/POST/DELETE 경쟁과 auth generation, 로그인/로그아웃 SSR viewer 갱신 | 느린 네트워크/계정 전환 결과 |
| 공동 | 목록 복귀 DOM 이벤트 또는 명시적 새 계약 | Android에서 작성/삭제/좋아요 후 목록 갱신 |
| 웹 | 없는 edit 메뉴/fallback /community, customText·tags 누락, 민감 로그 | 수정 SHA/계약 |
| 공동 | App Links/assetlinks/package/서명/Play fallback | 실제 배포 서명과 링크 검증 |
| 공동 | 세션 ACK, capabilities/version | 스키마 합의와 양쪽 배포. 현재 구현에 임의 추가하지 않음 |
| Android | 최소/대표/최신 WebView, HEIC/IME/회전/접근성 | 실기기 체크표 |

## 검증 기록

검증일 2026-09-02. 토큰/쿠키/로컬 설정 값은 기록하지 않는다.

- debug 단위 테스트: core:webview 8개, core:data 9개, core:ui 2개, feature:login 1개 통과.
  갱신 6개 시나리오에는 동시 성공/실패, logout 중 응답, 계정 교체, 이전 계정 REST 재시도 차단,
  일시적 네트워크 실패 보존이 포함된다.
- 실제 WebView 계측 테스트 1개 통과: 로컬 HTTP fixture를 loadUrl로 열고 AppBridge 호출,
  READY 전 queue, 반복 READY, JSON escape/한글 결과 전달 검증. 외부 서비스 변경 없음.
- debug APK 빌드, core:webview/data 및 feature:web release Kotlin 컴파일 통과.
- architecture guard와 git diff --check 통과.
- 개발 서버 /api/health, /community/3 HTTP 200 확인. health는 보호 API 정상의 증거가 아니다.
- Pixel 9 Pro XL 에뮬레이터, Android 16 / API 36, System WebView 144.0.7559.109:
  실제 피드 진입, 기존 세션으로 작성 열기, 상세 전체 화면, 하단 탭 숨김/복원,
  IME 닫기 후 작성 back 확인창, 계속 작성/나가기, 피드 상태/위치 유지 확인.
  재설치 후 익명 피드, 작성→로그인 안내→네이티브 로그인 표시→back 취소 복귀도 확인.
- 계측 테스트의 자동 정리로 에뮬레이터 앱이 제거되어 최종 debug APK를 다시 설치했다. 기존 로그인은 재진입이 필요하다. 최종 앱은 비로그인 피드로 열어두었다.
- 테스트 초안은 저장하지 않고 폐기. 게시글/댓글 등록, 삭제, 신고 제출은 실행하지 않음.

재현 명령:

```bash
./gradlew :core:webview:testDebugUnitTest :core:data:testDebugUnitTest :core:ui:testDebugUnitTest :feature:login:testDebugUnitTest :app:assembleDebug --no-daemon
./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.kyu.jiu_jitsu.web.WebViewTransportTest --no-daemon
./gradlew :core:webview:compileReleaseKotlin :core:data:compileReleaseKotlin :feature:web:compileReleaseKotlin --no-daemon
bash scripts/check-architecture.sh
```

아직 미검증: 실제 만료/refresh 실패 테스트 계정, 소셜 로그인 성공→웹 보류 동작 재개,
최소 OS/다른 WebView, 사진 선택 결과/HEIC/실제 업로드, 대형 글꼴/회전/프로세스 종료,
실제 변경 후 목록 DOM visibility 갱신, release R8 동작과 웹 쪽 POST/DELETE 경쟁.
계측 테스트는 웹 서버 기능을 대체하는 end-to-end 검수가 아니다.

## 사용 예와 구현 경계

- `WebContentRoute`는 app에서 로그인 slot, padding, fullscreen callback을 전달받는다.
  기본은 `/`, `initialPath = "/community/3"` 또는 정책 경로로 재사용할 수 있다.
  직접 상세 진입도 피드 부모+서브를 구성하여 웹 CLOSE가 동작한다.
- 앱의 기존 첫 홈 destination 키 RedScreen은 호환성을 위해 유지하고 표시 이름은 커뮤니티로 바꾼다.
- WebView 참조는 Composition 수명에만 둔다. 탭을 떠나거나 Activity가 재생성되면 파기된다.
  서브 화면 사이에는 부모 인스턴스를 보존한다. 영구 초안 복원은 이번 범위가 아니다.
- push/modal은 독립 전체 화면 컨테이너로 처리한다. 서로 다른 전환 애니메이션은 제공하지 않는다.
- 현재 경로 allowlist 외의 신규 웹 화면은 정책·테스트를 함께 추가한다. 없는 /edit를 앱에서 구현하지 않는다.
- bridge confirm은 파괴적 동작 표시와 가변 본문 스크롤이 필요하므로 기존 CommonDialog의
  고정 표면 대신 Material3 AlertDialog를 사용한다. 앱 디자인 확정 시 공통 컴포넌트 API를 확장할 수 있다.

