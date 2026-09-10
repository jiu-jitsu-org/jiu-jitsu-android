# `:feature:login` Agent Guide

This guide applies to everything under `feature/login/`. It extends the repository-root
[`AGENTS.md`](../../AGENTS.md) and the shared [`:core:ui` guide](../../core/ui/AGENTS.md).
The root guide and accepted ADRs win if instructions conflict.

## Feature Ownership

- Own social-login presentation, provider launch coordination, login state, and the signup-agreement
  sheet. Do not absorb nickname entry or profile behavior.
- Use `SnsLoginRepository`/`SessionRepository` public contracts and stable `:core:model` values. Never
  import Retrofit services, DTOs, session data sources, token providers, Hilt network modules, or
  repository implementations.
- Keep provider SDK details at the Android/UI integration boundary. Convert provider output to the
  repository's stable input without leaking SDK types into app models or other features.
- Do not put OAuth client IDs, tokens, provider responses, or personal data in logs, previews, tests,
  resources, or documentation. Read configured secrets only through the established build setup.

## State and Event Flow

- Prefer a Route/Screen split: the Route obtains the ViewModel, collects lifecycle-aware state, and
  handles navigation/provider launchers; the Screen renders immutable state and emits user actions.
- Consolidate touched state toward one immutable login state with explicit idle, in-progress,
  provider/error, existing-user, and signup-required behavior. Do not add another unrelated mutable
  state holder because legacy state is split.
- Treat navigation and provider launches as one-off effects. Do not navigate directly from ordinary
  recomposition or repeat a login exchange after configuration change.
- Disable or serialize provider actions while login is in progress. Preserve provider-specific
  cancellation separately from server/authentication failure when the user experience differs.
- Existing user success starts a durable session before entering signed-in UI. New-user success keeps
  only the temporary signup credential and opens the agreement flow; do not persist it as a completed
  session.
- Required agreements gate continuation. Marketing consent remains optional and must be derived from
  the user's actual selection.
- Render actionable loading/error/retry behavior. Never expose raw exception, HTTP, or provider error
  text directly to users.

## Adaptive Login UI

Follow the canonical
[Adaptive Compose UI](../../docs/development/adaptive-ui.md) contract. The app
root currently treats the login graph as edge-to-edge; the login background may draw behind bars,
while the screen content owner protects buttons, text, and sheet actions.

- The current bottom-aligned fixed button stack can exceed short or landscape windows. New or touched
  layouts must become vertically scrollable or use responsive spacing so every provider and skip
  action remains reachable in the canonical compact and landscape cases.
- Prefer horizontal `widthIn(max = ...)` plus outer padding on tablets/foldables so login buttons do
  not stretch indefinitely. Do not calculate button widths from the physical display.
- Decorative provider logos use null descriptions when the adjacent button label already names the
  action; otherwise provide a localized, non-duplicative description.
## Tests and Verification

Cover at least:

- Existing-user success versus signup-required branching.
- Required and optional agreement selection, dismiss, and resume behavior.
- Provider cancel, provider failure, backend failure, repeated tap suppression, and retry.
- State/effect behavior across recomposition or recreation.
- Compact, short/landscape, expanded-width, system-bar-present/absent, and long-text/font-scale UI.

Run from the Gradle root (`workspace/`):

```bash
./gradlew :feature:login:testDebugUnitTest :feature:login:compileDebugKotlin --no-daemon
bash scripts/check-architecture.sh
```

Manually verify real provider redirects only with non-production test accounts and without recording
credentials in logs or handoff notes.

## Screenshot Verification Policy

Screenshot-based verification is canceled by user direction. Do not add, run, or update screenshot
tests, reference images, or screenshot captures/reviews unless the user explicitly requests them
again. Use focused compilation, unit tests, and non-screenshot interaction checks as applicable.
