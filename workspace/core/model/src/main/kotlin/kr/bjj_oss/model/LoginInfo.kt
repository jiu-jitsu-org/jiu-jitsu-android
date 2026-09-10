package kr.bjj_oss.model

/**
 * Result of application-level social login.
 *
 * A new user normally receives [tempToken], while an existing user receives the persistent access
 * and refresh token pair. Token persistence is handled by SessionRepository, not by this model.
 */
data class LoginInfo(
    val accessToken: String,
    val refreshToken: String,
    val tempToken: String,
    val isNewUser: Boolean,
    val userInfo: UserInfo? = null,
)

/** User identity returned as part of successful authentication. */
data class UserInfo(
    val userId: Int,
    val email: String?,
    val nickname: String?,
    val profileImageUrl: String?,
    val snsProvider: String?,
)

/**
 * Complete authenticated-session snapshot persisted after login or sign-up.
 *
 * Keeping the write as one command makes it possible for the data layer to update encrypted
 * preferences atomically. A partially written token pair must never be observed by API requests.
 */
data class SessionUpdate(
    val accessToken: String,
    val refreshToken: String,
    val nickname: String? = null,
    val profileImageUrl: String? = null,
)
