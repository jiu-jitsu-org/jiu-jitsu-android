package kr.bjj_oss.web

/**
 * 기존 호출부 호환용 계약. 서브뷰는 이제 네이티브 상단 바 없이 WebView 전체 영역을 쓴다.
 * 루트 액션은 WebRootActions로 전달한다.
 */
data class WebSubviewActions(
    val onNotifications: (() -> Unit)? = null,
    val onMore: (() -> Unit)? = null,
)
