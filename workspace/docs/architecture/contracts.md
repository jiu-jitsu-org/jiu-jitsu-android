# API and Storage Contracts

This document is an inventory and navigation aid, not a replacement for backend OpenAPI documentation. Retrofit interfaces and DTOs are the current executable API contract. Update this inventory whenever an endpoint, authentication rule, payload family, or persisted key changes.

## API Configuration

- Backend base URL: supplied through BuildConfig from local project configuration.
- Endpoint constants: [`NetworkConfig.kt`](../../core/data/src/main/java/com/kyu/jiu_jitsu/data/utils/NetworkConfig.kt)
- Retrofit/OkHttp configuration: [`NetworkModule.kt`](../../core/data/src/main/java/com/kyu/jiu_jitsu/data/module/NetworkModule.kt)
- Service bindings and authenticated/unauthenticated clients: [`ApiModule.kt`](../../core/data/src/main/java/com/kyu/jiu_jitsu/data/module/ApiModule.kt)
- Request DTOs: `core/data/src/main/java/com/kyu/jiu_jitsu/data/model/dto/request/`
- Response DTOs: `core/data/src/main/java/com/kyu/jiu_jitsu/data/model/dto/response/`

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
- Server code `A0003` is treated as token expiration.
- `TokenRefreshInterceptor` serializes refresh attempts, persists new tokens, and retries the original request.
- Access and refresh tokens are stored through `SecurePreferences`.
- Current module-level access-token state is transitional and must be replaced by an injected token source.

## Local Storage

Current local persistence uses Preferences DataStore plus Android Keystore AES/GCM encryption. There is no Room database in the current module graph.

| Preference key | Stored value | Sensitivity |
| --- | --- | --- |
| `jjp_user_token` | Access token | Secret |
| `jjp_user_refresh_token` | Refresh token | Secret |
| `jjp_user_nick_name` | User nickname | Personal data |
| `jjp_user_profile_img` | Profile image reference/URL | Personal data |

Canonical implementation:

- [`PreferencesDatastore.kt`](../../core/data/src/main/java/com/kyu/jiu_jitsu/data/datastore/PreferencesDatastore.kt)
- [`SecureCrypto.kt`](../../core/data/src/main/java/com/kyu/jiu_jitsu/data/utils/SecureCrypto.kt)

## Contract Change Checklist

When changing an API or persisted value:

1. Update the service and request/response DTOs.
2. Update repository mapping so upper layers still receive stable app models.
3. Preserve backward compatibility or document the required migration.
4. Add tests for nullable fields, enum values, error envelopes, and auth behavior affected by the change.
5. Update this inventory.
6. Record an ADR if the change alters an architectural boundary rather than only an endpoint.

