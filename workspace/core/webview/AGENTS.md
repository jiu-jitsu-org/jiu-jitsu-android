# 공통 WebView 런타임

- 상위 AGENTS.md와 docs/development/webview.md를 따른다.
- 설정, URL 정책, 브리지 DTO/직렬화, 문서별 transport만 소유한다.
- data/domain/ui/feature/app에 의존하지 않는다. 인증 저장·로그인·게시글 API를 구현하지 않는다.
- Android View를 ViewModel/싱글톤에 보관하지 않는다. 문서 세대와 해제를 검사한다.
- 정책과 프로토콜 변경은 순수 JVM 테스트로 검증한다. 실제 WebView 동작은 기기에서 검증한다.

## OPEN_SUBVIEW 확장 — 구현 반영, 사용자 검증 대기

- [상단 바·부모 보존 방향](../../docs/workstreams/web-subview-native-chrome.md)을 따른다. 2026-09-07 후속 요청으로 구현 승인됨. 이번 변경의 테스트·작업 검토는 사용자가 직접 진행한다.
- 서브 화면도 기존 WebViewPage/WebPageView와 transport를 재사용한다. 상단 바·알림·더보기 UI를 런타임에 넣지 않는다.
- 부모 유지 목적의 loadUrl/reload/clearHistory 또는 전역 pauseTimers를 추가하지 않는다. onPause를 JS 완전 정지로 설명하지 않는다.
- 문서 세대와 원래 응답 대상 검증을 유지하고 미합의 chrome/ACK 메시지를 추가하지 않는다.
