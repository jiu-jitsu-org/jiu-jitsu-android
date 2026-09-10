# Operations

This document records the currently evidenced build, release, and incident-response process. It must not imply automation or ownership that does not exist in the repository.

## Current Build Artifacts

APK and AAB artifacts remain in the standard module build directories: `app/build/outputs/apk/`
and `app/build/outputs/bundle/`. The former `workspace/outputs/` directory and automatic copy tasks
were removed at the user's request. Do not recreate them.

Common commands:

```bash
./gradlew :app:assembleDebug --no-daemon
./gradlew :app:assembleRelease --no-daemon
./gradlew :app:bundleRelease --no-daemon
```

Artifact names follow the Android Gradle plugin defaults; no timestamped duplicate is produced.

## Current Release Constraints

- The Android application ID is `kr.bjj_oss`; module namespaces and source packages use this prefix.
  This is a separate installed app identity, so an existing installation under the previous ID is
  not upgraded and its private data/session is not automatically migrated.
- The checked-in debug Firebase configuration has been aligned with the new package for local
  builds. This does not register an app with Firebase. Before service verification, supply a
  configuration downloaded for `kr.bjj_oss` and update the Android package/signing registrations
  used by Firebase, Google sign-in, and Kakao. Release Firebase configuration is not checked in.

- Release minification is currently disabled.
- Version code and version name are currently defined directly in `app/build.gradle.kts`.
- Firebase configuration is required by the Google Services plugin.
- Signing and store-publishing procedures are not documented in this repository.
- No CI workflow, automated deployment pipeline, or documented rollback mechanism was found.

Do not claim a production release is ready until signing, environment selection, Firebase configuration, backend compatibility, and publishing ownership are confirmed.

## Release Checklist

Before producing a release candidate:

1. Confirm target backend environment and local configuration without printing secrets.
2. Confirm `versionCode` and `versionName`.
3. Run applicable checks from [Development](../development/index.md).
4. Verify login, signup, session restoration, profile read/update, token refresh, image upload, and FCM behavior.
5. Remove or disable sensitive debug logging.
6. Confirm signing configuration and artifact ownership with the responsible maintainer.
7. Build the release APK/AAB and record the exact command and artifact.
8. Verify installation and critical flows on a release build.
9. Record known issues and rollback criteria.

## Incident Response

For a production-impacting issue:

1. Preserve evidence: app version, build type, device/OS, timestamps, affected journey, backend environment, logs, and reproduction steps.
2. Protect sensitive data: redact tokens, personal data, Firebase values, and signing information.
3. Classify impact: crash/startup, authentication/session, data corruption, privacy/security, backend compatibility, or degraded feature.
4. Contain the issue using the safest available mechanism. Do not invent a remote kill switch or rollback path that has not been implemented.
5. Reproduce and identify the owning layer before patching.
6. Verify the fix with focused tests and the affected end-to-end journey.
7. Record the cause, impact, resolution, and prevention work in a dedicated incident document when the issue is material.

## Missing Operational Documentation

The following must be supplied by the project owner before a fully repeatable production process exists:

- Release owner and approval policy.
- Signing-key custody and recovery process.
- Play Console publishing procedure.
- Environment and backend promotion strategy.
- Monitoring, crash-reporting, alerting, and severity definitions.
- Rollback or staged-rollout procedure.
- Privacy and data-retention requirements.

