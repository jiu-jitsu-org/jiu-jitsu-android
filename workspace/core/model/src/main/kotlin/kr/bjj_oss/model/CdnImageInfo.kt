package kr.bjj_oss.model

/** App-facing metadata for an image registered with the backend after CDN upload. */
data class CdnImageInfo(
    val id: Int,
    val cdnId: String,
    val imageUrl: String,
    val status: String,
)
