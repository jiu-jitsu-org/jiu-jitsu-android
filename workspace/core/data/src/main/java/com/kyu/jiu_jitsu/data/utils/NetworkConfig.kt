package com.kyu.jiu_jitsu.data.utils

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

    object User {
        const val USER = "api/user"
        const val CHECK_NICKNAME = "api/user/check/nickname"
    }

    object UserController {
        const val USER_PROFILE = "api/user/profile"
    }

    object CommunityProfileController {
        const val COMMUNITY_PROFILE = "api/community/profile"

    }

}