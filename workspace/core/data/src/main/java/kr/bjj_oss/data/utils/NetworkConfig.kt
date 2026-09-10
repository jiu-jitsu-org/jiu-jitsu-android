package kr.bjj_oss.data.utils

object NetworkConfig {
    // Test Random User REST API
    object RandomUser {
        const val RESULTS = "api/"
    }

    object BootStrap {
        const val INFO = "api/bootstrap/info"
    }

    object Authentication {
        const val SNS_LOGIN = "api/auth/sns-login"
        const val REFRESH = "api/auth/refresh"
        const val LOGOUT = "api/auth/logout"
    }

    object Image {
        const val AUTH = "api/image/auth"
        const val IMAGE = "api/image"
    }

    object User {
        const val USER = "api/user"
        const val CHECK_NICKNAME = "api/user/check/nickname"
        const val USER_APP_INFO = "api/user/appInfo"
    }

    object UserController {
        const val USER_PROFILE = "api/user/profile"
        const val USER_PROFILE_IMAGE = "api/user/profile/image"
    }

    object CommunityProfileController {
        const val COMMUNITY_PROFILE = "api/community/profile"

    }

}
