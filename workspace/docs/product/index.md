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
