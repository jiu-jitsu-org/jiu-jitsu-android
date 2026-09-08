# 공통 WebView 개발 지침

상태: Current. [분석·작업 계획](../workstreams/shared-webview.md)과
[ADR 0003](../architecture/decisions/0003-isolate-shared-webview-runtime.md)을 먼저 읽는다.

## OPEN_SUBVIEW 네이티브 상단 바

[상단 바·부모 보존 구현 방향](../workstreams/web-subview-native-chrome.md)에 따라
기존 Activity 안의 독립 WebView 스택을 재사용한다. 서브 화면에는 항상 네이티브 상단 바를
표시하고 로딩/오류는 본문에만 표시한다. app이 흰색 상태바 배경을 그린다.
일반 뒤로가기는 보호 처리 후 히스토리 우선이며, CLOSE_SUBVIEW는 명시적 닫기다.
WebContentRoute의 subviewActions(url)에서 WebSubviewActions callback을 전달하면 알림/더보기를
개별 표시한다. 기본값은 숨김이며 기존 웹 payload는 변경하지 않는다.
2026-09-07 구현 요청에서 사용자가 테스트·작업 검토를 직접 진행하기로 했으므로 현재 미검증이다.
웹 헤더 중복 제거와 알림/더보기 목적지·노출 정책은 공동 작업으로 남아 있다.

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
