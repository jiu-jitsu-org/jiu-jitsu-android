# 공통 웹 화면

- 상위 AGENTS.md와 docs/development/webview.md, adaptive-ui.md를 따른다.
- 웹 스택, 인증 repository 조합, dialog/sheet/chooser/share를 소유한다.
- 다른 feature 구현에 의존하지 않는다. 로그인은 app이 주입하는 UI slot으로 연결한다.
- 모든 비동기 결과는 원래 문서에만 회신한다. app chrome은 callback으로 요청한다.
- 부모 WebView를 유지하고 최종 제거 시 정리한다. 회전 후 초안 복원을 주장하지 않는다.
