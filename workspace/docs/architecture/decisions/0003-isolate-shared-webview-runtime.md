# ADR 0003: 공통 WebView 런타임과 화면 조합 분리

- 상태: Accepted
- 날짜: 2026-09-02
- 근거: [웹 인수인계](../../../TO_ANDROID_DEV.md), ADR 0001/0002

## 배경

커뮤니티 피드·상세·작성과 정책 화면은 동일한 Next 서버와 11개 요청/7개 응답
브리지를 사용한다. 기존 프로젝트에 WebView 구현은 없다. `:core:ui`에 인증·네트워크를
추가하거나 Activity 하나에 모든 로직을 모으면 기존 NIA 경계를 깨뜨린다.

## 결정

- `:core:webview`: URL 정책, 타입 검증, 문서 수명, 전송, WebView 생성/해제.
  data/domain/feature 의존성을 갖지 않는다. 전송 DTO는 앱 모델이 아니므로 이 모듈 소유다.
- `:feature:web`: 공통 웹 화면, 메인/서브 스택, 네이티브 표면, 파일 선택, 인증 조합.
  `:core:webview`, `:core:data`의 repository API, `:core:ui`만 이용한다.
- `:core:data`: 네이티브/웹이 공유하는 토큰 갱신 및 인증 세대, BFF 세션과 쿠키 준비.
- `:app`: 홈 진입 연결, 하단 탭/시스템 inset, 로그인·닉네임 화면 조합.
  웹 feature는 로그인 feature를 직접 참조하지 않고 composable slot을 받는다.

## 대안과 결과

`core:ui` 통합은 플랫폼 런타임과 디자인 책임이 섞여 제외한다. 커뮤니티 전용 WebView는
정책·소개 화면에서 중복되므로 제외한다. 별도 browser Activity는 기존 목록과 로그인 UI
조합을 복잡하게 하므로 Compose 내 보존 스택을 사용한다. 부모 WebView는 숨겨도 유지하고,
제거/Activity 재생성에는 파기한다. React 초안·File의 재생성 복원은 보장하지 않는다.

현재 AppBridge 계약은 호출 frame의 origin을 인증할 수 없다. 신뢰된 웹과 외부 iframe을
차단하는 웹 CSP가 배포 전제다. AndroidX 메시징 전환은 웹 adapter와 공동 변경으로 남긴다.

## 이행

기존 core:data/domain 분리는 유지한다. 공통 refresh 경로만 추출하여 중복 rotation을 막고,
기존 REST 요청의 1회 재시도 계약은 유지한다. 구조 검사를 새 모듈까지 확대한다.
