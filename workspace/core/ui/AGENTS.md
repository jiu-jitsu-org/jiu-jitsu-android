# `:core:ui` Agent Guide

This guide applies to everything under `core/ui/`. It extends the repository-root
[`AGENTS.md`](../../AGENTS.md); the root guide and accepted ADRs win if instructions conflict.

## Purpose and Boundary

- Own app-specific reusable Compose UI shared by multiple features, shared async UI contracts, and
  model-to-visual mappings that are genuinely cross-feature.
- The current theme and generic primitives live here only until `:core:designsystem` is justified and
  introduced. Keep new generic tokens/primitives separable from app-model-aware composites.
- Do not depend on `:core:data`, `:core:domain`, `:app`, or feature modules. Shared UI may depend on
  stable `:core:model` types only when the component is intentionally app-specific.
- Do not move feature-only components, form state, navigation side effects, or feature `UiState` here
  to avoid duplication without demonstrated reuse.
- Route definitions centralized here are migration debt. Do not add a new feature destination here
  when the owning feature can expose its navigation API and `:app` can compose it.

## Compose API Rules

- Reusable composables accept `modifier: Modifier = Modifier` and apply it once to their outermost
  meaningful node. Callers own placement; components own internal spacing.
- Prefer stateless parameters and event callbacks. Do not acquire feature ViewModels, NavControllers,
  activities, or repositories inside shared leaf components.
- Do not force `fillMaxSize`, system-bar padding, or screen background from a leaf component unless
  that behavior is the documented component contract.
- Use resource-backed user-facing text and meaningful semantics/content descriptions. Keep decorative
  images `contentDescription = null`.
- Use at least 48 dp effective touch targets for interactive controls. Prefer `heightIn(min = ...)`
  over a fixed height when localized or scaled text can grow.
- Fixed `dp` is acceptable for icons, strokes, and intentional minimum control sizes. Do not use a
  fixed page height, device-width arithmetic, or spacer compensation for a particular screenshot.
- Preserve the caller's modifier order intentionally: decide whether background draws behind insets
  before adding padding, clipping, or click handling.

## Adaptive UI and Screenshot Ownership

Follow the canonical
[Adaptive Compose UI and Screenshot Tests](../../docs/development/adaptive-ui.md) contract for
edge-to-edge, inset ownership, status-bar height, adaptive constraints, IME, accessibility, and the
320/360/600 dp, landscape, and font-scale screenshot matrices.

This module additionally owns:

- Shared multi-preview annotations in `ui/tooling/AdaptivePreviews.kt`. Change the canonical matrix
  only together with the development document and all affected reference images.
- Runtime inset helpers. `StatusBarBackground` remains decoration only; do not turn it into a hidden
  screen-level padding owner.
- The deterministic shared screenshot contract fixture. It verifies shared configuration and
  components but does not replace screenshots of a feature's real stateless Screen/component.
- Screenshot plugin configuration for this module. Keep preview fixtures out of `main` unless they
  are reusable tooling annotations or deliberate preview-only sample data.

Run from the Gradle root (`workspace/`):

```bash
./gradlew :core:ui:testDebugUnitTest :core:ui:compileDebugKotlin --no-daemon
./gradlew :core:ui:validateDebugScreenshotTest --no-daemon
bash scripts/check-architecture.sh
```

Compile at least one affected feature consumer when changing a shared composable API.
