# MY profile

- Status: Implemented; device/UI and live-server verification reserved for the user.
- Updated: 2026-11-10
- Source specification: repository-root `myprofile.md`, checked against the local iOS MyProfile
  sources and asset catalog. This document records Android behavior, not proof of backend responses.

## Presentation and navigation

`ProfileScreen` connects repository-backed `ProfileUiState` to the stateless `MyProfileContent`.
`ModifyProfileScreen` retains the legacy push destination using the same screen and a Back action.
The app owns edge-to-edge and status-bar icon contrast; the feature reports header contrast and
insets its controls below the runtime status bar. Scroll content accounts for the root bottom bar
or navigation inset. Content is centered and limited to 680 dp; edit forms to 600 dp (nickname 560).
Forms scroll with one IME owner. Narrow widths and large fonts stack the favorite cards.

The profile action toolbar is a fixed, always-transparent overlay above the LazyColumn, including
the owner-verification menu and settings actions. Only the system status-bar area (clock/battery)
changes background on scroll: transparent at the exact origin, white at 80% opacity after scrolling.
That background uses the actual runtime status-bar height and is separate from the action toolbar.
The first colored header reserves the measured toolbar height including its top inset, so its avatar
starts below the controls. This space scrolls with the first item; later content passes behind the
transparent toolbar. System-bar icon contrast remains owned by the app.

The header uses the user API for the avatar/OWNER badge and the community profile, then session,
for the nickname. Nickname, academy, belt, favorite style, and competition actions open their
existing native edit flows. Guests skip profile requests and see Login and Settings actions.
Settings continues to use the existing settings feature; this change does not alter its notification
persistence or legal/account flows.

The belt-colored header, belt artwork, avatar, style illustrations, and medal vectors use iOS
catalog assets at their declared densities. The unregistered state depends only on favorite styles
and competitions, independently of belt, academy, and best styles. The populated state shows
competition records before three favorite cards.

## Reads and saves

Community and user profile reads run independently on entry and resume. Partial failure preserves
successful data. Community load failure has Retry; background refresh preserves existing content.
Repository reads and community mutations are serialized. Session generation invalidates pending
cache writes and queued work after an account changes. Nickname changes invalidate older profile
reads. The repository remains the shared community source of truth for every editor.

Belt saves use `BELT`, without prompting for gender or weight. Other sections retain gender, weight,
visibility, best styles, and owner teaching data. Academy deletion sends an empty string. Favorite
selection may be cleared; its edited JSON key is explicitly null. Style registration saves position,
technique, and submission separately; Skip leaves previously saved steps intact. A failed save keeps
the editor and its draft. Nickname edit checks the exact 2–12 character Hangul/ASCII-letter/digit
input, then requires a second tap to save the same checked value. Stale checks are canceled/discarded.

The belt sheet uses the same `BeltRankPickerContent` layout as BeltRankAndWeightBottomSheet:
white background, 20 dp horizontal/15 dp vertical padding, a belt title, side-by-side snapping belt
and stripe wheels, and the shared primary button. At normal font size, wheel items are 44 dp high
and 86 dp wide with an 8 dp gap. Larger fonts scale the item height while widths respect the offered
space; the sheet content scrolls in short windows. BeltPickerSheet retains saveable draft indices,
initializes from the current profile (WHITE/STRIPE_0 when absent), and confirms through the existing
BELT update callback. Its button says Confirm because this entry saves belt/stripe immediately.

## Competitions

The UI retains original division indices, even for equal-valued records. Year and normalized name
group divisions for display, with pin status and highest belt at group level. Wire grouping instead
uses year and the trimmed original name; nested `competitions[].divisions` is the current contract.
The old flat `competitionInfoList` is accepted only as a read fallback.

Add proceeds through result, name/autocomplete, year, division, server save, and completion. Adding
another division fixes the tournament name/year and reuses the last division's uniform/belt.
Editing replaces one original division; editor deletion removes one division. Group deletion asks
for confirmation and removes every division in that display group. The final deletion sends `[]`.
Autocomplete is debounced/cancelable; editing the selected name/year clears the linked tournament ID.
Unlimited weight and absolute are exclusive; legacy OVER kilograms survive unrelated edits.

Up to three tournament groups may be pinned. Long-press drag, accessibility move actions, and explicit
move buttons edit an order draft within pin boundaries. Cancel discards it; Done saves one full list.
A failed order save reveals the unchanged repository snapshot. Refresh reconciles current rows with
the draft. Medal totals come from the server rather than recounting local records.

## Images and verification

The system Photo Picker requires no storage permission. Camera capture uses an app FileProvider URI
and requests camera permission only when selected. Avatar selection opens a square crop with drag,
zoom, and accessible position sliders. Cache URI/crop coordinates survive recreation; bounded bitmap
decode and JPEG processing run off the main thread. Uploads are limited to 1024 px (avatar) or
2560 px (verification), JPEG quality 85. Verification bypasses crop to preserve the document.

Image work completes signing, anonymous ImageKit upload, server registration, and account linking
before success. Image IDs are Long. Profile deletion calls the server, restoring the prior avatar
on failure. Local previews are cleared on failure and retained after success. Duplicate image writes
are blocked. Verification uses NONE/PENDING/REJECTED/APPROVED sheets, records PENDING after successful
submission, and blocks input/Back while uploading. An OWNER with no verification history sees the
approved sheet; ADMIN alone does not receive the badge.

Community activity pushes the shared web stack at `/community/me`, using the configured origin and
session coordinator. Closing its initial subview returns to MY; nested subviews retain normal stack
and guard/history behavior. No separate token cache or production domain is introduced.

## Verification

- `./gradlew :app:assembleDebug --no-daemon`: final build passed on 2026-11-10 (11 seconds).
- `git diff --check`: passed.
- Architecture scan: blocked by the existing DTO import in `feature/setting/.../SettingViewModel.kt`
  (`UpdateNoticeSettingRequest`), also present at HEAD before this change.
- No UI, screenshot, emulator, unit, or live-server tests run, per the requested build-only scope.
- Device acceptance remains with the user: compact/landscape, foldable/tablet resizing, large fonts,
  IME, camera/picker cancellation, nested web Back, save failures, and server null-clearing behavior.

### Navigation serializer fix (2026-11-10)

The initial implementation declared serializable destinations in nickname/profile without applying
the serialization compiler plugin to those feature modules. A successful APK build did not prove
serializer generation; NavHost registration could fail at app startup. Both modules now apply
`libs.plugins.kotlin.serialization` and declare the serialization runtime explicitly.

Verification after this fix: `:app:assembleDebug` passed (24 seconds). A temporary JVM smoke check
used KClass-based serializer lookup and JSON round trips for EditNicknameDestination,
CompetitionEditorDestination (default and populated arguments), and ProfileCommunityDestination;
all passed. Bytecode inspection confirmed generated serializer methods, and the APK contains the
generated nickname/competition serializer classes. No device launch or UI test was performed.
The architecture scan still reports the separate, pre-existing SettingViewModel DTO import.

### Style editor UI restoration (2026-11-10)

By user request, ModifyMyStyleScreen restores the original Android presentation: best/favorite
segmented tabs, original card artwork and descriptions, DraggableFlipCard front/back, mini-card
selector, translucent header, and gradient-backed bottom action. Short windows scroll the card;
expanded widths remain bounded and large fonts allow the tabs/footer to grow.

The screen signature and existing category navigation are unchanged. The current ViewModel state,
repository commands, success events, retry, and per-category registration remain in use. Completing
the Best tab opens Favorite locally; completing Favorite saves that category once. Best values are
sent only when explicitly edited, preserving untouched server values. Re-tapping a selected favorite
still clears it. Both selections and the active tab survive recreation; failed saves retain drafts.
Skip exits without saving the current draft. Registration advances position → technique → submission
after successful saves, while individual-category editing closes after one successful save.

Verification: `./gradlew :app:assembleDebug --no-daemon` passed (30 seconds); `git diff --check`
passed. UI/device/screenshot tests were not performed, as requested. The architecture scan continues
to report the existing SettingViewModel DTO import, unrelated to this restoration.
