# API and Storage Contracts

This document is an inventory and navigation aid, not a replacement for backend OpenAPI documentation. Retrofit interfaces and DTOs are the current executable API contract. Update this inventory whenever an endpoint, authentication rule, payload family, or persisted key changes.

## API Configuration

- Backend base URL: supplied through BuildConfig from local project configuration.
- Endpoint constants: [`NetworkConfig.kt`](../../core/data/src/main/java/kr/bjj_oss/data/utils/NetworkConfig.kt)
- Retrofit/OkHttp configuration: [`NetworkModule.kt`](../../core/data/src/main/java/kr/bjj_oss/data/module/NetworkModule.kt)
- Service bindings and authenticated/unauthenticated clients: [`ApiModule.kt`](../../core/data/src/main/java/kr/bjj_oss/data/module/ApiModule.kt)
- Stable repository result and app models: [`core/model`](../../core/model/)
- Request DTOs: `core/data/src/main/java/kr/bjj_oss/data/model/dto/request/`
- Response DTOs: `core/data/src/main/java/kr/bjj_oss/data/model/dto/response/`

Do not place real base URLs, OAuth secrets, Firebase configuration, or signing material in this document.

## Backend Endpoint Inventory

| Method | Path | Authentication | Retrofit source | Purpose |
| --- | --- | --- | --- | --- |
| GET | `api/bootstrap/info?osName=ANDROID` | No | `BootStrapService` | Bootstrap and app-version information |
| POST | `api/auth/sns-login` | No | `LoginService` | Exchange social-login data for application login/signup state |
| POST | `api/auth/refresh` | Refresh token | `TokenRefreshInterceptor` | Refresh access and refresh tokens after expiration |
| GET | `api/user/profile` | Bearer token | `UserService` | Read the user profile |
| PUT | `api/user/profile` | Bearer token | `UserService` | Update basic user profile |
| PUT | `api/user/profile/image?imageFileId=...` | Bearer token | `UserService` | Apply a registered profile image |
| POST | `api/user` | Bearer-token client currently used | `UserService` | Complete signup |
| GET | `api/user/check/nickname?nickname=...` | Bearer-token client currently used | `UserService` | Check nickname availability |
| POST | `api/user/appInfo` | Bearer token | `UserService` | Register device/application information |
| GET | `api/community/profile` | Bearer token | `CommunityService` | Read the community profile |
| POST | `api/community/profile` | Bearer token | `CommunityService` | Update the community profile |
| GET | `api/image/auth` | Bearer token | `ImageService` | Obtain ImageKit upload authorization |
| POST | `api/image` | Bearer token | `ImageService` | Register uploaded image metadata |

The external ImageKit upload uses `POST https://upload.imagekit.io/api/v1/files/upload` through `ImageKitService`.

Authentication assignments above describe the Retrofit client currently provided by `ApiModule`. Confirm backend policy before changing a service between authenticated and unauthenticated clients.

## Token Behavior

- Authenticated requests add an `Authorization: Bearer <token>` header.
- An empty credential is omitted rather than sent as `Authorization: Bearer `.
- Server code `A0003` is treated as token expiration.
- `TokenRefreshCoordinator` serializes REST/WebView refresh attempts, reuses a token refreshed by another request, and commits only to the same session revision. `TokenRefreshInterceptor` retries each original REST request at most once.
- `SessionRepository` is the public session boundary used by app and feature ViewModels.
- Starting or clearing a session also clears repository-owned user profile memory so one account's profile cannot flash for the next account.
- Access and refresh tokens are encrypted through `SecurePreferences`; `AccessTokenProvider` is the injected in-memory view used by synchronous OkHttp header interception.
- A new user's temporary sign-up token stays memory-only until sign-up returns a durable access/refresh pair.
- General network timeouts are 30 seconds. NetworkModule clients use the debug-only profiler; the dedicated refresh and WebView session clients never profile credentials, never redirect, and have an 8-second call timeout.

## Local Storage

Current local persistence uses Preferences DataStore plus Android Keystore AES/GCM encryption. There is no Room database in the current module graph.

| Preference key | Stored value | Sensitivity |
| --- | --- | --- |
| `jjp_user_token` | Access token | Secret |
| `jjp_user_refresh_token` | Refresh token | Secret |
| `jjp_user_nick_name` | User nickname | Personal data |
| `jjp_user_profile_img` | Profile image reference/URL | Personal data |

Canonical implementation:

- [`PreferencesDatastore.kt`](../../core/data/src/main/java/kr/bjj_oss/data/datastore/PreferencesDatastore.kt)
- [`SecureCrypto.kt`](../../core/data/src/main/java/kr/bjj_oss/data/utils/SecureCrypto.kt)
- [`SessionLocalDataSource.kt`](../../core/data/src/main/java/kr/bjj_oss/data/session/SessionLocalDataSource.kt)

## Contract Change Checklist

When changing an API or persisted value:

1. Update the service and request/response DTOs.
2. Update repository mapping so upper layers still receive stable app models.
3. Preserve backward compatibility or document the required migration.
4. Add tests for nullable fields, enum values, error envelopes, and auth behavior affected by the change.
5. Update this inventory.
6. Record an ADR if the change alters an architectural boundary rather than only an endpoint.

## WebView BFF session

`WebSessionRepository` prepares the frontend `POST /api/auth/session` with raw accessToken and validates
HTTP 200 + success + authenticated. Host-only HttpOnly session cookies are applied through
CookieManager callbacks before navigation. Anonymous entry expires only oss_session at path /.
Web frontend configuration uses separate debug/release origin settings, independent of API base URLs.
JWT exp is an advisory refresh trigger; opaque tokens remain subject to protected API validation.

The 11 incoming / 7 outgoing bridge types are defined in `core:webview/BridgeMessages.kt` and
[the handoff](../../TO_ANDROID_DEV.md). The native refresh budget is 8 seconds within the web's
15-second recovery window; the web BFF POST still needs time after native delivery. Transient network
failure sends AUTH_SESSION_EXPIRED to the waiting web document but preserves encrypted native tokens
for a later retry. This current policy must be confirmed with the backend/product before release.
Session changes increment revision; no refreshToken crosses the bridge. Cookie state and web-memory
state are distinct. Web session POST/DELETE races remain a coordinated web release requirement.
