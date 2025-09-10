package com.kyu.jiu_jitsu.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RandomUserResponse(
    val results: List<User>,
    val info: Info
)

@JsonClass(generateAdapter = true)
data class User(
    val name: Name,
    val email: String,
    val picture: Picture
) {
    val fullName: String get() = "${name.first} ${name.last}"
}

@JsonClass(generateAdapter = true)
data class Name(val title: String, val first: String, val last: String)

@JsonClass(generateAdapter = true)
data class Picture(val large: String, val medium: String, val thumbnail: String)

@JsonClass(generateAdapter = true)
data class Info(val seed: String, val results: Int, val page: Int, val version: String)

