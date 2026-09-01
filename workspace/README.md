# JiuJitsuPjt Android

This directory is the Android Gradle project root for the Jiu-Jitsu application.

## Project Snapshot

- Kotlin and Jetpack Compose
- Material 3
- Hilt and KSP
- Retrofit, OkHttp, and Moshi
- Preferences DataStore with Android Keystore encryption
- Navigation Compose
- Gradle Kotlin DSL, version catalog, and included `build-logic`

## Documentation

Start with [`docs/index.md`](docs/index.md).

- Product scope: [`docs/product/`](docs/product/index.md)
- Architecture and contracts: [`docs/architecture/`](docs/architecture/index.md)
- Architecture decisions: [`docs/architecture/decisions/`](docs/architecture/decisions/index.md)
- Development and verification: [`docs/development/`](docs/development/index.md)
- Release and incident response: [`docs/operations/`](docs/operations/index.md)
- Active migrations: [`docs/workstreams/`](docs/workstreams/index.md)

Repository working rules are in [`AGENTS.md`](AGENTS.md).

## Quick Build

```bash
./gradlew :app:assembleDebug --no-daemon
```

The app build requires `app/google-services.json` or a variant-specific Google Services configuration. See [Development](docs/development/index.md) for module-level verification commands and [Operations](docs/operations/index.md) for release constraints.
