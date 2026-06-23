package com.kyu.jiu_jitsu.domain.usecase.image

import com.kyu.jiu_jitsu.data.api.common.UiState
import com.kyu.jiu_jitsu.data.model.CdnImageInfo
import com.kyu.jiu_jitsu.data.model.dto.response.toInfo
import com.kyu.jiu_jitsu.data.repository.ImageRepository
import com.kyu.jiu_jitsu.domain.mapApiResponseToUiState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UploadCommunityImageUseCase @Inject constructor(
    private val imageRepository: ImageRepository,
) {
    suspend operator fun invoke(
        imageUri: String,
        publicKey: String,
    ): Flow<UiState<CdnImageInfo>> =
        imageRepository.uploadCommunityImage(
            imageUri = imageUri,
            publicKey = publicKey,
        ).mapApiResponseToUiState { data -> data.toInfo() }
}
