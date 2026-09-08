# 공통 웹 화면

- 상위 AGENTS.md와 docs/development/webview.md, adaptive-ui.md를 따른다.
- 웹 스택, 인증 repository 조합, dialog/sheet/chooser/share를 소유한다.
- 다른 feature 구현에 의존하지 않는다. 로그인은 app이 주입하는 UI slot으로 연결한다.
- 모든 비동기 결과는 원래 문서에만 회신한다. app chrome은 callback으로 요청한다.
- 부모 WebView를 유지하고 최종 제거 시 정리한다. 회전 후 초안 복원을 주장하지 않는다.

## OPEN_SUBVIEW 확장 — 구현 반영, 사용자 검증 대기

- [상단 바·부모 보존 방향](../../docs/workstreams/web-subview-native-chrome.md)을 읽는다. 2026-09-07 후속 요청으로 구현 승인됨. 이번 변경의 테스트·작업 검토는 사용자가 직접 진행한다.
- 기존 단일 Activity의 entry 스택을 확장한다. 부모를 재생성하지 않고 자식만 새 WebViewPage로 만든다.
- 네이티브 상단 바와 알림/더보기 표시 상태는 이 모듈 소유다. 시스템 바는 app에 의미 있는 상태로 요청한다.
- 일반 뒤로가기는 보호 처리 후 히스토리 우선, 명시적 CLOSE_SUBVIEW는 최상단 닫기로 구분한다.
- 웹 헤더 중복과 알림/더보기 동작·노출 계약은 미확정이다. 임의 브릿지 메시지나 무반응 버튼을 추가하지 않는다.
