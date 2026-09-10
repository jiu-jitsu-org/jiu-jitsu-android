# `:feature:profile` Agent Guide

This guide applies to everything under `feature/profile/`. It extends the repository-root
[`AGENTS.md`](../../AGENTS.md) and the shared [`:core:ui` guide](../../core/ui/AGENTS.md).
The root guide and accepted ADRs win if instructions conflict.

## Feature Ownership

- Own community-profile display/edit screens, profile image selection, academy, belt/stripe, gender,
  weight visibility, competition history, and Jiu-Jitsu style presentation.
- Use `CommunityRepository`, `ImageRepository`, and other public repository contracts with stable
  `:core:model` commands/results. Do not import services, DTOs, DataStore/session internals, Hilt
  network modules, or repository implementations.
- Keep feature screen state and visual selection models here. Move a model to `:core:model` only when
  it represents stable cross-layer application meaning; move a UI component to `:core:ui` only after
  demonstrated cross-feature reuse.
- Reusable business validation/transformation may live in `:core:domain`; simple profile reads and
  writes may call repository contracts directly.

## State and Update Flow

- Prefer one immutable state per screen with explicit loading, empty/content, update-in-progress,
  error, and retry behavior. Do not add more parallel state holders when touching legacy split state.
- Route composables obtain ViewModels, collect state lifecycle-aware, own permission/activity-result
  launchers, and translate one-off navigation. Screen composables render state and emit actions.
- Repository-observed profile state is the source of truth. Do not recreate a mutable profile singleton
  or retain a second account-scoped cache in UI code.
- Build explicit update commands from edited values. Update visible profile state only after success
  unless optimistic behavior and rollback are intentionally implemented.
- Saved-state result keys are navigation contracts. Centralize and type them when touched; consume or
  clear handled results so recomposition does not repeat refresh/update work.
- Keep loading/error behavior local to the operation so one failed edit does not erase already loaded
  profile content. Prevent duplicate uploads and writes.
- Replace touched hard-coded display strings with resources. Rank/style model values map to localized
  text and visual resources in UI-owned code, not in `:core:model`.

## Profile Image and Permission Rules

- Keep camera/gallery launch and Android `Uri` handling at the Route/UI boundary. Send stable upload
  input through the established repository API; do not put `Activity`, launcher, or bitmap state in a
  ViewModel.
- Prefer the system Photo Picker where available. Request legacy media permission only on OS versions
  that actually require it, and request camera permission only for camera capture.
- Use app-owned `FileProvider` URIs and cache files. Never log local paths, upload signatures, image
  authorization, or personal image URLs.
- Decode previews with bounds/sample limits and off the main thread when work can be material. Handle
  cancellation, process recreation, invalid URI, decode failure, upload failure, and retry.
- Deletion must distinguish local preview removal from server-side profile-image deletion; do not
  imply remote deletion until the repository operation succeeds.

## Adaptive Profile UI

Follow the canonical
[Adaptive Compose UI](../../docs/development/adaptive-ui.md) contract. Profile
contains both an edge-to-edge colored header and protected edit/form screens, so preserve that
feature-specific distinction explicitly.

- On the main profile screen, the belt-colored background may extend behind the status bar, but header
  text and actions must start below `WindowInsets.statusBars`/the owned top padding. Painting a
  `StatusBarBackground` during scroll does not inset or protect those controls.
- Edit screens that apply root `padding` must not also apply `systemBarsPadding`. If a design opts into
  edge-to-edge for one region, split the background from inset content and apply only the needed sides.
- Keep system-bar icon contrast coordinated by `:app`; a feature must expose semantic chrome intent or
  background information rather than calling `enableEdgeToEdge` or mutating the Activity window.
- Replace fixed terminal spacers such as bottom-bar compensation with `LazyColumn`/scroll content
  padding derived from the root bottom-bar and navigation insets.
- Profile and edit content must scroll when height is constrained. Primary actions may be pinned only
  when the remaining content still scrolls and the action is IME/navigation-safe.
- Use the composable's constraints for cards and media. Preserve meaningful aspect ratios, apply
  `widthIn(max = ...)` for readable tablet layouts, and avoid stretching profile forms/cards across an
  entire expanded window.
- On foldables/tablets, a bounded single pane is acceptable; introduce a list-detail/two-pane layout
  only with a clear product requirement and named window/posture input.
- Verify draggable/flip cards and wheel pickers under narrow width, landscape height, touch
  exploration, and large font. Do not let gestures make the only path to a selection.
## Tests and Verification

Cover the changed paths, especially:

- Profile loading/content/empty/error/retry and refresh after returning from an edit.
- Update command generation and success/failure state retention for academy, belt/weight, competition,
  and style changes.
- Permission grant/deny, picker/camera cancel, decode failure, upload failure/retry, and process-state
  restoration for image work.
- Compact phone, short landscape, expanded width, status bar present/absent, gesture/three-button
  navigation, IME-visible edit forms, long resources, and large font scale.
- Scroll-transition status-bar background and system-bar icon contrast on each header color.

Run from the Gradle root (`workspace/`):

```bash
./gradlew :feature:profile:testDebugUnitTest :feature:profile:compileDebugKotlin --no-daemon
bash scripts/check-architecture.sh
```

Also run affected `:core:model`, `:core:domain`, or `:core:data` tests when profile contracts or
mapping behavior changes, and manually verify camera/gallery behavior on applicable API levels.

## Screenshot Verification Policy

Screenshot-based verification is canceled by user direction. Do not add, run, or update screenshot
tests, reference images, or screenshot captures/reviews unless the user explicitly requests them
again. Use focused compilation, unit tests, and non-screenshot interaction checks as applicable.
