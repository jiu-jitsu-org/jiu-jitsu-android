package com.kyu.jiu_jitsu.data.repository

interface RefreshTokenRepository {
    suspend fun refreshToken()
}