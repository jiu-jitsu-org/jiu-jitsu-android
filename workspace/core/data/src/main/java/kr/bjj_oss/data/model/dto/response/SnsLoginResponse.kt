package kr.bjj_oss.data.model.dto.response

import kr.bjj_oss.model.LoginInfo
import kr.bjj_oss.model.UserInfo
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SnsLoginResponse(
    override val success: Boolean?,
    override val code: String?,
    override val message: String?,
    override val data: SnsLoginResponseData?,
) : ApiResponse<SnsLoginResponseData>

@JsonClass(generateAdapter = true)
data class SnsLoginResponseData(
    val isNewUser: Boolean?,
    val tempToken: String?,
    val accessToken: String?,
    val refreshToken: String?,
    val userInfo: UserInfoResponseData?,
)

@JsonClass(generateAdapter = true)
data class UserInfoResponseData(
    val userId: Int,
    val email: String?,
    val nickname: String?,
    val profileImageUrl: String?,
    val snsProvider: String?,
)

/** Converts nullable wire fields to the stable authentication result consumed by features. */
internal fun SnsLoginResponseData.toInfo(): LoginInfo =
    LoginInfo(
        accessToken = accessToken.orEmpty(),
        refreshToken = refreshToken.orEmpty(),
        tempToken = tempToken.orEmpty(),
        isNewUser = isNewUser ?: true,
        userInfo = userInfo?.toInfo(),
    )

private fun UserInfoResponseData.toInfo(): UserInfo = UserInfo(
    userId = userId,
    email = email,
    nickname = nickname,
    profileImageUrl = profileImageUrl,
    snsProvider = snsProvider,
)
