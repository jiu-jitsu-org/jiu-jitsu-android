# Product

## Product Scope

This repository contains a native Android client for a Jiu-Jitsu profile and community experience. The product description below is limited to behavior evidenced by the current code. New requirements must be documented before implementation when they materially change a user journey.

## Current User Journeys

### Startup and session

- Start from a splash/bootstrap flow.
- Load bootstrap and app-version information.
- Restore locally stored login/session information when available.
- Route the user to authentication or the signed-in experience.

### Authentication and signup

- Select a supported social-login provider.
- Authenticate with the provider and exchange provider information with the backend.
- Distinguish an existing user from a user who must complete signup.
- Collect signup agreements and a nickname.
- Check nickname availability before signup completion.

### Profile

- Display the signed-in user's community profile.
- Update basic profile information and profile image.
- Update academy, belt, stripe, gender, weight, and weight visibility.
- Maintain competition history and Jiu-Jitsu style information such as positions, techniques, and submissions.

### Device registration

- Receive an FCM token.
- Send application/device information to the backend when the integration is wired.

## Current Product Gaps

The following gaps are visible in the current implementation and are not completed requirements:

- Google login UI does not complete the full click-to-login flow.
- Apple login is represented but not implemented.
- Some profile-edit actions still display placeholder text.
- FCM token updates are not fully connected to startup and refresh behavior.

Architecture-only migration work is tracked separately in [the NIA migration workstream](../workstreams/nia-architecture-migration.md).

## Adding or Changing Product Requirements

Document material feature work using the following minimum structure:

1. Problem and intended user.
2. User-visible behavior and primary journey.
3. Loading, empty, success, error, retry, and offline behavior.
4. Authentication, privacy, and permission implications.
5. Analytics or operational signals, if required.
6. Acceptance criteria.
7. Explicit non-goals.

Do not infer backend capability, business policy, or release commitment from placeholder UI or partially implemented code.

## Product Completion

A user-facing change is complete only when its acceptance criteria are met and the applicable checks in the [definition of done](../development/definition-of-done.md) pass.

## 커뮤니티 웹 화면

홈 첫 탭은 Next 피드를 표시한다. 루트에는 스크롤되지 않는 네이티브 상단 바를 두고
피드/카테고리 탭과 알림/검색/프로필 아이콘을 표시한다. 웹 본문만 스크롤한다.
카테고리는 앱의 상대 경로 설정을 사용하며 미설정/미허용 주소는 비활성화한다.
알림과 프로필 아이콘은 각각 알림 웹 화면과 내 커뮤니티 웹 화면을 자식 WebView로 연다.
이는 네이티브 알림 수신 설정이나 하단 MY 탭 이동과 별도다. 목적지 설정이 유효하지 않으면 해당 아이콘은 비활성화한다.
검색 아이콘 목적지는 TODO다. 자식 복귀 시 기존 루트 WebView를 다시 표시한다.
서브뷰에는 네이티브 상단 바/빈 헤더 영역 없이 WebView를 표시한다.
루트 탭 전환은 스택을 쌓지 않고 문서를 교체하며 탭별 웹 상태 보존은 범위에 포함하지 않는다.
 상세/작성은 목록을 보존한 전체 화면 WebView로 열고,
로그인/가입·확인창·신고 선택·사진 선택·공유는 네이티브 UI와 연결한다.
설정 누락/세션 준비 실패/문서 오류는 안내와 재시도를 제공한다. 회전 후 웹 초안/선택 사진은
복원하지 않는다. 실제 배포 검수와 웹 측 출시 조건은 [작업 계획](../workstreams/shared-webview.md)을 따른다.

## Settings

- The main settings tab groups notifications, legal links, app version, and account actions.
- Notifications and withdrawal are visible only for an authenticated session. The account action
  reads Login for guests and Logout for signed-in users.
- Login opens the existing native login flow. Logout clears the local session and cached profile
  through the existing session repository, matching web logout; server revocation is not implemented.
- Version information displays the installed build's version name.
- Signed-in users open a native notification screen from Settings. It has its own back-stack entry;
  toolbar and system Back return to the retained Settings entry. Bottom tabs are hidden on this detail.
- The notification screen shows account/security, service, community, and marketing switches, with
  design defaults on/on/on/off. Toggles are UI-only and survive recreation of the current entry;
  leaving and reopening starts with defaults. No notification delivery, consent, server, or persistent
  preference is changed. Connecting real preference storage is a separate follow-up.
- Service terms, privacy policy, and withdrawal still open temporary `example.com` browser URLs.
  These flows must be replaced before release; account deletion is not implemented.

Settings verification (2026-09-08): debug app build and architecture boundary scan passed; four
session-state/logout unit tests and fourteen adaptive screenshot cases passed. Emulator checks
confirmed signed-in rows, logout switching to guest rows, and guest login opening the native login
screen. Temporary detail pages and real account deletion remain outside this verification.

Native notification screen verification (2026-09-10): setting compilation and unit tests,
`:app:assembleDebug`, and the architecture boundary scan passed. Emulator UI-hierarchy checks
confirmed signed-in Settings → Notifications, hidden bottom tabs, on/on/on/off defaults, marketing
switch interaction, and both system and toolbar Back returning to Settings. No screenshots were
captured; guest visibility retains the existing session check.
