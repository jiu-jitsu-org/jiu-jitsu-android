package com.kyu.jiu_jitsu.ui.routes

import kotlinx.serialization.Serializable

@Serializable object SplashScreen

@Serializable object HomeGraph
@Serializable object ProfileScreen
@Serializable object RedScreen
@Serializable object BlueScreen
@Serializable object GrayScreen

@Serializable object LoginGraph
@Serializable object LoginScreen

@Serializable data class NickNameScreen(val isMarketingAgreed: Boolean)

@Serializable object ProfileGraph
@Serializable object ModifyProfileScreen
@Serializable data class ModifyAcademyScreen(val academyName: String = "")
@Serializable data class ModifyMyStyleScreen(val styleType: String = "")
@Serializable object ModifyCompetitionScreen


sealed class SkillStyleScreenType(val screenName: String) {
    object Position : SkillStyleScreenType(screenName = "position")
    object Technique : SkillStyleScreenType(screenName = "technique")
    object Submission : SkillStyleScreenType(screenName = "submission")
    object ALL: SkillStyleScreenType(screenName = "all")
}