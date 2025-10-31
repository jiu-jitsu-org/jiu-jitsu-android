package com.kyu.jiu_jitsu.ui.routes

import kotlinx.serialization.Serializable

@Serializable object SplashScreen

@Serializable object HomeGraph
@Serializable object RedScreen
@Serializable object BlueScreen
@Serializable object GrayScreen

@Serializable object LoginGraph
@Serializable object LoginScreen

@Serializable data class NickNameScreen(val isMarketingAgreed: Boolean)