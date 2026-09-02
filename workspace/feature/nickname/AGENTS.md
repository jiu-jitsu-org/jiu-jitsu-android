# `:feature:nickname` Agent Guide

This guide applies to everything under `feature/nickname/`. It extends the repository-root
[`AGENTS.md`](../../AGENTS.md) and the shared [`:core:ui` guide](../../core/ui/AGENTS.md).
The root guide and accepted ADRs win if instructions conflict.

## Feature Ownership

- Own nickname input, local validation presentation, availability-check transitions, and signup
  completion initiated from this screen.
- Put reusable nickname validation/normalization policy in `:core:domain` when more than this feature
  owns the rule. Keep form focus, entered text, button labels, and error resources in this feature.
- Use repository contracts and stable models only. Do not import user services, request/response DTOs,
  session data sources, preference keys, or repository implementations.
- Preserve the marketing-consent value supplied by the signup journey; never infer consent from
  successful validation or from submitting required agreements.

## State and Validation Flow

- Prefer one immutable screen state containing input, validation status, availability status,
  submission status, and user-displayable error reason. Route code collects it lifecycle-aware;
  stateless Screen code emits actions.
- Define normalization once. The value checked for availability must be the value submitted, unless
  an explicit backend contract documents a transformation between them.
- Editing input after an availability success invalidates that success immediately. A response for an
  older input must not enable signup for the newer input; cancel, key, or discard stale requests.
- Separate local validation failure, duplicate nickname, network failure, and signup failure so retry
  and button behavior are unambiguous.
- Prevent duplicate check/signup requests while the corresponding operation is in progress.
- Emit navigation as a one-off effect only after signup and durable-session establishment succeed.
- Keep all visible titles, hints, button labels, and errors in this feature's or shared UI resources.
  Do not surface raw backend messages.

## Adaptive Nickname Form

Follow the canonical
[Adaptive Compose UI and Screenshot Tests](../../docs/development/adaptive-ui.md) contract.
`NickNameScreen` receives root `PaddingValues`; it owns applying that contract and the form's IME
avoidance without duplicating the same sides.

- Avoid a page structure that can push the primary action below a short/landscape window. Use a
  scrollable content region and a navigation/IME-safe action region, or a single inset-aware scroll
  container with derived content padding.
- The 200 dp placeholder/art area is not a reason to clip the form. Scale, constrain, or move
  nonessential artwork before sacrificing input or action accessibility on compact heights.
- Bound form width on tablets/foldables while retaining outer padding on narrow phones. Use offered
  constraints, not physical screen dimensions.
- Screenshot idle, local-validation error, duplicate, available, submitting, and failure states using
  stateless content. The IME case injects the canonical test inset; real keyboard behavior is still
  verified on an emulator/device.

## Tests and Verification

Cover at least:

- Blank, whitespace, boundary length, Unicode/composition, invalid, available, and duplicate inputs.
- Editing after success, out-of-order availability responses, repeated taps, network retry, and signup
  failure/success.
- Marketing consent forwarded unchanged and navigation emitted only once.
- Compact/short landscape, expanded width, status-bar-present/absent, IME visible, long strings, and
  large font scale.

Run from the Gradle root (`workspace/`):

```bash
./gradlew :feature:nickname:testDebugUnitTest :feature:nickname:compileDebugKotlin --no-daemon
./gradlew :feature:nickname:validateDebugScreenshotTest --no-daemon
bash scripts/check-architecture.sh
```

Also run `:core:domain:test` when nickname validation policy changes there.
