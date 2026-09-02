package com.kyu.jiu_jitsu.data.repository

import com.kyu.jiu_jitsu.model.AppResult
import com.kyu.jiu_jitsu.model.CdnImageInfo

/** Coordinates CDN upload, backend registration, and profile-image activation. */
interface ImageRepository {

    suspend fun uploadCommunityImage(
        imageUri: String,
        publicKey: String,
    ): AppResult<CdnImageInfo>
}
