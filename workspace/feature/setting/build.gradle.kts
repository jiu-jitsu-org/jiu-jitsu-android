// 설정 기능 모듈의 Compose, Hilt, 공통 UI 설정을 기존 convention plugin으로 구성한다.
plugins {
    alias(libs.plugins.jjs.android.feature)
}

android {
    namespace = "com.kyu.jiu_jitsu.setting"
}

// Repository나 공유 모델을 실제 사용할 때 필요한 core 의존성만 추가한다.
