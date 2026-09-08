# Workstreams

Workstream documents track large, multi-change efforts that cannot be safely completed in one task. They record scope, sequencing, temporary exceptions, and completion criteria.

## Active

| Workstream | Status | Goal |
| --- | --- | --- |
| [NIA architecture migration](nia-architecture-migration.md) | Active | Align module and public type boundaries with Now in Android |

## Workstream Rules

- Keep feature backlogs out of this directory unless they span multiple coordinated changes.
- Update status and completed milestones in the same change that reaches them.
- Record blockers and scope changes explicitly.
- When complete, mark the workstream complete and link the resulting ADRs and final architecture documents.


- [공통 WebView 도입](shared-webview.md): Active — Android 구현과 웹 공동 검수.

## 구현 반영 / 검증 대기

- [OPEN_SUBVIEW 네이티브 상단 바](web-subview-native-chrome.md): 코드 반영 — 기존 부모 보존 스택 확장, 네이티브 상단 바와 뒤로가기 정책. 사용자 요청에 따라 테스트·작업 검토 미실행.
