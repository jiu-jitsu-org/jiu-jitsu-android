# Adaptive Compose UI

- Status: Current
- Applies to: `:core:ui`, `:feature:login`, `:feature:nickname`, `:feature:profile`,
  `:core:webview`, `:feature:web`, and app-level Compose scaffolding

This document is the canonical project contract for edge-to-edge Compose layout, runtime system
insets, adaptive device behavior, and IME handling. Module-level
`AGENTS.md` files should contain only ownership rules and feature-specific exceptions, then link here
instead of copying this contract.

## Verification Policy

Screenshot-based verification was canceled by the user on 2026-09-08. Do not add, execute, or
update screenshot tests, reference images, screenshot captures, or image-comparison reviews unless
explicitly requested again. Existing screenshot tooling and fixtures in other modules are historical
assets, not required checks. Adaptive layout and accessibility requirements below still apply.

## Ownership Model

The application and each screen have different responsibilities:

| Owner | Responsibility |
| --- | --- |
| `:app` root | Call `enableEdgeToEdge`, select destination chrome behavior, set system-bar icon contrast, own the root `Scaffold`, and pass its `PaddingValues` contract |
| Route composable | Obtain the ViewModel, collect state lifecycle-aware, own launchers/navigation, and pass stable state plus layout inputs to the Screen |
| Screen composable | Decide which background draws edge-to-edge, apply each owned inset once, arrange scroll/action regions, and render immutable state |
| Shared component | Honor caller constraints and `Modifier`; do not mutate the window or silently add screen-level system insets |

Do not mutate Activity window flags or system-bar appearance from a feature or leaf component. A
screen that needs different chrome should expose semantic intent to the app-level owner.

## System Insets Contract

System bars and display geometry are runtime inputs, not design constants.

- Never assume a 24 dp status bar, a fixed navigation-bar height, or that either bar exists.
- Read `WindowInsets`; a top or bottom inset of zero is valid in immersive, freeform, preview,
  desktop-style, and some test environments.
- Build a top region as `runtime status-bar inset + app-bar content minimum`, not as one fixed combined
  height. With no status bar, only the app-bar content minimum remains.
- Apply every inset exactly once. Do not stack root `PaddingValues`, `Scaffold` defaults,
  `systemBarsPadding`, and `windowInsetsPadding` for the same side.
- Use the narrowest source and side: `statusBars` for top chrome, `navigationBars` for bottom actions,
  `ime` for keyboard avoidance, and `safeDrawing` when all drawing hazards must be protected.
- A background may draw behind a system bar while text and controls remain inset. Use separate
  background and protected-content nodes rather than padding the background itself.
- `consumeWindowInsets` transfers ownership to descendants. Do not use it only to mask accidental
  double padding.
- Check the actual inset behavior of `Scaffold`, `ModalBottomSheet`, navigation containers, and other
  parents before adding child padding.

`currentStatusBarHeight()` reports the current runtime value. `StatusBarBackground` only paints that
area; neither function protects content or consumes the inset.

## Modifier and Component Rules

- Reusable composables accept `modifier: Modifier = Modifier` and apply it once to the outermost
  meaningful node. Callers own placement; components own internal spacing.
- Do not force `fillMaxSize`, system-bar padding, or a screen background from a leaf component unless
  that behavior is its documented contract.
- Preserve modifier order deliberately: background, clipping, click handling, inset padding, and
  caller padding produce different geometry depending on their order.
- Use at least 48 dp effective touch targets. Prefer `heightIn(min = ...)` to fixed heights for
  text-bearing controls.
- Fixed `dp` values are acceptable for icons, strokes, intentional minimums, and bounded artwork.
  Do not use fixed page heights or terminal `Spacer` values to compensate for a specific device.
- Keep user-facing strings in resources. Decorative images use `contentDescription = null`; actionable
  controls use localized and non-duplicative semantics.

## Adaptive Layout Rules

Use the space offered to the composable, not cached physical-display dimensions.

- Prefer constraints, weights, `widthIn`, `heightIn`, aspect ratios, lazy/scroll containers, and
  bounded readable content widths.
- The test widths in this document are regression fixtures, not product breakpoints. Do not branch in
  production merely because a test uses 320, 360, or 600 dp.
- If compact and expanded composition differs materially, pass a named window-size/posture input from
  the app or screen boundary.
- Narrow phones must avoid horizontal clipping; short landscape windows must keep every action
  reachable; tablets/foldables must avoid uncontrolled full-width stretching; multi-window must react
  to live constraint changes.
- Long/localized text and large font scale must reflow instead of overlap. Use ellipsis only when
  truncation is an explicit product rule and the full value remains accessible.
- Scrolling content uses bottom content padding derived from the root bottom bar and navigation
  insets, not a fixed final spacer.
- Use `start`/`end` and layout-direction-aware `PaddingValues`; verify RTL navigation affordances.

## IME and Form Rules

- Keep the focused field, validation/error text, and primary action reachable while the IME is open.
- Apply IME avoidance at the form or screen content owner. Do not add `imePadding` independently to
  several nested children.
- Use an IME-aware scroll/action arrangement and focus/keyboard actions. Fixed vertical centering is
  not sufficient for short windows.
- Hardware-keyboard and zero-IME-inset configurations must retain normal spacing.
- Compose Preview/Layoutlib does not open the real platform keyboard. Use relevant device or emulator
  interaction checks for focus, resize behavior, keyboard actions, and navigation-bar interaction.

## Adaptive Layout Cases

Use these representative dimensions when evaluating layout behavior. They do not require screenshot
capture or image-comparison tests.

| Case | Viewport | Purpose |
| --- | --- | --- |
| Compact | `320 x 568 dp` | Narrow and short phone; catches horizontal clipping and fixed-height overflow |
| Phone | `360 x 800 dp` | Normal phone reference |
| Expanded | `600 x 960 dp` | Tablet/foldable single-pane bounds and excessive stretching |
| Landscape | `800 x 360 dp` | Short-height scrolling and action reachability |
| Font scale | `360 x 800 dp` at `1.0`, `1.3`, and `2.0` | Text wrapping, control growth, and overlap |
| IME visible | `360 x 800 dp`, real keyboard visible | Focused form and primary-action reachability |

Include status-bar presence, navigation mode, cutouts, RTL, long text, or other cases when relevant
to the changed UI. Use non-screenshot interaction checks and focused tests in proportion to risk.

## Completion Checklist

- Insets are owned and applied once; status/navigation bar absence remains valid.
- Content and actions are reachable in compact and landscape viewports.
- Expanded width is intentionally bounded or intentionally adaptive.
- Large text does not overlap or make actions inaccessible.
- The IME scenario keeps the focused field and primary action reachable.
- Relevant device interaction checks cover behavior that compilation and unit tests cannot verify.
