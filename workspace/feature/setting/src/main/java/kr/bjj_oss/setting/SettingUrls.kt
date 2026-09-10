package kr.bjj_oss.setting

/** Temporary destinations authorized for UI integration; these are not production service pages. */
internal object SettingUrls {
    // TODO: Replace with the real notification detail URL and its authentication contract.
    const val NOTIFICATIONS = "https://example.com/notifications"
    // TODO: Replace with the published service terms URL.
    const val TERMS = "https://example.com/policies/terms-of-service"
    // TODO: Replace with the published privacy policy URL.
    const val PRIVACY = "https://example.com/policies/privacy-policy"
    // TODO: Replace with the account withdrawal page. No account-deletion API is invoked here.
    const val WITHDRAWAL = "https://example.com/account/withdrawal"
}
