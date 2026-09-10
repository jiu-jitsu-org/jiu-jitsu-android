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

홈 첫 탭은 Next 피드를 표시한다. 상세/작성은 목록을 보존한 전체 화면 WebView로 열고,
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
- Notification details, service terms, privacy policy, and account withdrawal currently open
  user-authorized `example.com` placeholder URLs in a browser. These URLs and detail flows must be
  replaced before release; notification preferences and account deletion are not implemented.

Settings verification (2026-09-08): debug app build and architecture boundary scan passed; four
session-state/logout unit tests and fourteen adaptive screenshot cases passed. Emulator checks
confirmed signed-in rows, logout switching to guest rows, and guest login opening the native login
screen. Temporary detail pages and real account deletion remain outside this verification.
