# Shared Web Screen

- Follow the parent [AGENTS.md](../../AGENTS.md), [WebView development](../../docs/development/webview.md), and [Adaptive UI](../../docs/development/adaptive-ui.md) guides.
- Own the web stack, authentication repository orchestration, and dialogs, sheets, choosers, and sharing.
- Do not depend on other feature implementations. Connect login through a UI slot injected by the app.
- Deliver every asynchronous result only to its originating document. Request app chrome changes through callbacks.
- Preserve parent WebViews and clean them up when permanently removed. Do not claim that drafts are restored after rotation.

## OPEN_SUBVIEW Extension — Implemented, Awaiting User Verification

- Read the [top-bar and parent-preservation direction](../../docs/workstreams/web-subview-native-chrome.md). Implementation was authorized in the follow-up request on 2026-09-07. The user will perform testing and work review for this change.
- Extend the existing entry stack within the single Activity. Create only the child as a new WebViewPage without recreating the parent.
- This module owns the native top bar and notification/more visibility state. Request system-bar changes from the app by exposing semantic state.
- Distinguish ordinary back navigation, which prioritizes web history after applying guards, from an explicit CLOSE_SUBVIEW request, which closes the top entry.
- The contracts for avoiding duplicate web headers and for notification/more behavior and visibility remain unresolved. Do not invent bridge messages or add buttons that do nothing.
