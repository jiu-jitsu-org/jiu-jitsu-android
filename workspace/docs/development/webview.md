# 공통 WebView 개발 지침

상태: Current. [분석·작업 계획](../workstreams/shared-webview.md)과
[ADR 0003](../architecture/decisions/0003-isolate-shared-webview-runtime.md)을 먼저 읽는다.

## 루트 상단 바와 OPEN_SUBVIEW

2026-10-20 요구사항으로 네이티브 상단 바는 루트에만 표시한다.
`entries.size == 1 && !loginVisible`에서 피드/카테고리와 알림/검색/프로필이 보이며,
루트 entry의 Column 안에 고정 헤더와 남은 높이의 WebView를 배치한다.
외부 Compose 컨테이너는 스크롤하지 않는다. 상단/하단 시스템 inset은 기존 소유권을 유지한다.
서브뷰는 네이티브 상단 바나 헤더 여백 없이 WebView 전체 영역을 사용한다.
모든 entry의 composition과 document.id 키는 유지하며 부모 entry 전체를 숨긴다.
부모 헤더 공간은 자식 진입 시 제거하지 않지만 app 하단 바/padding 변경으로 인한 viewport 변화까지
없다고 보장하지는 않는다. 복귀 시 스크롤/DOM 동작은 실제 웹과 기기에서 검증해야 한다.

피드는 기존 `/`로 연결한다. 카테고리는 AppNavHost의 `categoryPath = null` TODO를 실제 같은-origin
상대 경로로 교체하고 WebUrlPolicy/계약 테스트에 허용 경로를 등록한 뒤 활성화한다.
탭 전환은 루트 문서를 교체하며 자식/브라우저 히스토리를 쌓지 않는다. 탭별 DOM/스크롤은 보존하지 않는다.
선택 상태는 host의 rootPath로 관리하며 웹 내부 라우트 변경과의 자동 동기화는 후속 계약이다.
동일 URL 재선택은 정상 문서에서 무시하며 실패 문서는 다시 생성한다.
보호 중인 문서, 로그인/표면/파일 선택/세션 준비 중에는 탭 전환을 차단한다.

알림/내 커뮤니티는 AppNavHost에서 BuildConfig의 COMMUNITY_NOTIFICATION_PATH/COMMUNITY_ME_PATH를
상대 경로로 전달한다. Route는 아이콘별 경로 유효성을 표시하고 host.openNativeSubview로 진입한다.
WebUrlPolicy.resolveScreenPath가 런타임 origin과 조합하며 전체 URL/프로토콜 상대 URL/미허용 경로는 거절한다.
현재 허용 목적지는 `/community/notification`, `/community/me`이며 하위 경로를 포괄 허용하지 않는다.
누락/공백/잘못된 경로는 해당 아이콘을 비활성화한다. 검색은 기존 TODO로 남아 있다.
네이티브 push는 루트에서만 허용하며 세션 준비/로그인/네이티브 표면/파일 선택/유효 문서의 back guard 중에는
진입하지 않는다. 첫 push 직후 루트 조건이 해제되어 연속 클릭으로 중복 push하지 않는다.
자식은 기존 OPEN_SUBVIEW와 동일한 initializeBridgeOnPageFinished 옵션으로 생성하고 부모에는 탐색을 강제하지 않는다.
로그인이 필요하면 기존 웹 로그인 브리지 흐름을 사용하며 네이티브에서 인증 요구를 추측하지 않는다.
기존 subviewActions/WebSubviewActions는 호출부 호환을 위해 유지하나 더 이상 UI에 적용하지 않는다.
서브뷰 복귀는 시스템 Back 또는 웹의 기존 CLOSE_SUBVIEW를 사용한다.
일반 Back의 IME/guard/히스토리 처리와 부모 WebView 보존 정책은 유지한다.
이전 투명 서브뷰 상단 바 요구사항은 [과거 구현 기록](../workstreams/web-subview-native-chrome.md)을 참고한다.

## 소유권과 구현 순서

1. 웹 메시지/화면 계약 변경 여부를 인수인계 문서와 대조한다.
2. URL·전송·수명 처리는 core:webview, 화면/표면 처리는 feature:web에 둔다.
3. 인증·BFF HTTP·쿠키는 core:data repository를 통해 처리한다.
4. 앱은 로그인 화면 slot과 chrome 상태만 연결한다. pass-through UseCase를 추가하지 않는다.
5. 순수 정책/상태 테스트 → 모듈 컴파일 → 앱 빌드 → 실기기 검수를 진행한다.

## 반드시 지킬 계약

- origin은 scheme/host/port로 검증한다. userinfo, 임의 scheme, API 화면, 미구현 경로는 거절한다.
- 개발 WEB_ORIGIN과 운영 WEB_ORIGIN은 독립 설정이다. API 주소로부터 추측하지 않는다.
- AppBridge.postMessage(String)를 모든 인스턴스에 로드 전에 등록한다.
- JSON 필드 타입을 검증하고 JSON serializer로 JS 문자열을 escape한다. 전체 payload를 로그에 쓰지 않는다.
- 문서 시작마다 generation/ready/guard/대기 결과를 초기화한다. READY 전 요청은 수신하고 응답만 대기한다.
- onPageFinished 자체를 READY나 인증 ACK로 간주하지 않는다. 아래 임시 예외에서는 동일한 준비 검사를 시도하는 계기로만 사용한다. 반복 READY에 토큰/reload를 반복하지 않는다.
- 결과는 webViewId + generation + requestId에 묶는다. 재생성·닫힘 이후 결과는 폐기한다.
- 확인창·시트는 한 번만 응답한다. 표시 불가면 dismiss한다. back guard 중에는 BACK_PRESSED 후 기다린다.
- 초기 SSR 전 쿠키 callback을 기다린다. CookieJar와 CookieManager는 별도 저장소다.
- refreshToken은 브리지로 노출하지 않는다. 전체 앱 갱신은 한 경로로 직렬화한다.
- logout/계정 전환은 auth generation을 올려 늦은 refresh 성공을 폐기한다.
- 서브 화면을 열 때 부모를 유지한다. 돌아올 때 reload/가짜 DOM 이벤트로 갱신을 흉내 내지 않는다.
- 시스템 바는 app, 전달받은 padding/IME 적용은 웹 화면이 소유한다. WebView를 세로 스크롤에 넣지 않는다.
- 파일 선택은 표준 chooser만 사용하고 취소/문서 교체에 null을 한 번 반환한다. 업로드는 웹 책임이다.
- SSL 오류는 cancel, mixed content/file access는 금지, third-party cookie는 비활성화한다.
- onConsoleMessage 원문 수집과 인증 HTTP profiler는 금지한다.

## 빌드 설정

`local.properties` 또는 Gradle `-P`에 따옴표 없는 origin 값을 지정한다.
`DEV_WEB_ORIGIN`은 debug, `WEB_ORIGIN`은 release에만 사용한다. 기본값은 사용자에게 확인한 개발/운영 프런트엔드다.
빈 값 또는 잘못된 값을 주입하면 앱은 연결 준비 안내를 표시한다. 운영은 HTTPS만 허용한다.
로컬 HTTP는 debug의 localhost/127.0.0.1/10.0.2.2만 허용한다. 권장 경로는
`adb reverse tcp:3000 tcp:3000` + `http://localhost:3000`이다.
빌드 설정과 실제 Next BFF의 백엔드 환경을 반드시 함께 확인한다.

## 검증

```bash
./gradlew :core:webview:testDebugUnitTest :core:data:testDebugUnitTest :feature:web:compileDebugKotlin :app:assembleDebug --no-daemon
bash scripts/check-architecture.sh
```

실기기에서는 인수인계 §12.2를 사용한다. 필수 항목은 첫 SSR/만료/동시 refresh/logout 경쟁,
작성 back/IME, 같은 requestId 격리, chooser 취소·회전, 목록 visibility 복귀,
오프라인/SSL/READY timeout, 최소/대표/최신 WebView와 큰 글꼴이다.
실제 웹 서버 없이 웹 기능 검수 통과를 선언하지 않는다. WebView는 Layoutlib 스크린샷으로
검증할 수 없으므로 플랫폼 UI/웹 화면은 기기 검수, 순수 네이티브 UI는 필요 시 별도 preview를 쓴다.

## 출시 전 공동 조치

웹의 세션 GET/POST/DELETE 경쟁, 로그인 후 SSR 갱신, 목록 복귀 이벤트,
민감 로그, 없는 수정 경로, 신고 customText, 태그, Android 설치 fallback은 앱만으로 고칠 수 없다.
명시적 복귀/세션 ACK/capabilities는 현재 계약에 없으므로 임의 메시지를 추가하지 않는다.
계정 전환은 기존 WebView를 폐기하고 새 쿠키를 준비한다. 이미 서버에서 진행 중인 웹 세션
쓰기의 완전한 취소는 보장할 수 없으므로 웹의 세대 검사/직렬화 완료가 출시 조건이다.

참고: [WebView API](https://developer.android.com/reference/android/webkit/WebView),
[브리지 보안](https://developer.android.com/privacy-and-security/risks/insecure-webview-native-bridges),
[CookieManager](https://developer.android.com/reference/android/webkit/CookieManager),
[파일 chooser](https://developer.android.com/reference/android/webkit/WebChromeClient).

## 재사용 API

`WebContentRoute(padding, onFullscreenChanged, loginContent, initialPath)`를 app 내에서 조합한다.
`initialPath`는 `/`, `/community/3`, 정책 경로 등 allowlist에 있는 상대 경로다.
`loginContent`는 완료 callback에 성공 true, 건너뛰기/뒤로가기 false를 전달한다.
다른 앱 화면에서도 같은 runtime을 사용할 때는 직접 WebView를 새로 설정하지 말고
`WebViewPage`와 `WebPageView`를 사용한다. 허용 경로가 늘면 WebUrlPolicy와 계약 테스트를 함께 변경한다.

## 계측 테스트 환경

이 환경의 Gradle connectedDebugAndroidTest 실행기는 종료 후 대상 앱을 자동 제거했다.
기존 로그인/웹 저장소를 보존해야 하는 개발 기기 대신 테스트 전용 에뮬레이터에서 실행한다.
테스트 후 `./gradlew :app:installDebug`로 최종 앱을 다시 설치하고 필요하면 재로그인한다.

## OPEN_SUBVIEW READY 임시 보완

사용자 요청으로 OPEN_SUBVIEW에서 생성한 WebView에만 initializeBridgeOnPageFinished를 켠다.
웹 WEBVIEW_READY와 onPageFinished는 같은 tryInitializeBridge(generation)를 사용하며,
현재 문서/origin/수신 함수 검사에 성공했을 때만 READY 처리와 초기 인증 전달을 실행한다.
루트 및 initialPath 직접 진입은 기존 웹 READY 방식이며, OPEN_SUBVIEW 자식의 다시 시도는 옵션을 유지한다.
수신 함수 미등록만 즉시 실패시키지 않고 다음 완료/READY 이벤트를 기다린다. 검사 도중 완료 이벤트는
한 번 보관해 재시도하며, 성공 후 중복 초기화는 차단한다. 기존 20초 제한과 HTTP/SSL 오류 처리는 유지한다.
페이지 완료 이후에도 내부 웹 처리기가 준비되지 않은 문제까지 해결하는 계약은 아니다.
테스트·빌드·작업 검토는 사용자 요청에 따라 실행하지 않았다.
