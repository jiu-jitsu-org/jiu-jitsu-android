# 공통 WebView 런타임

- 상위 AGENTS.md와 docs/development/webview.md를 따른다.
- 설정, URL 정책, 브리지 DTO/직렬화, 문서별 transport만 소유한다.
- data/domain/ui/feature/app에 의존하지 않는다. 인증 저장·로그인·게시글 API를 구현하지 않는다.
- Android View를 ViewModel/싱글톤에 보관하지 않는다. 문서 세대와 해제를 검사한다.
- 정책과 프로토콜 변경은 순수 JVM 테스트로 검증한다. 실제 WebView 동작은 기기에서 검증한다.
