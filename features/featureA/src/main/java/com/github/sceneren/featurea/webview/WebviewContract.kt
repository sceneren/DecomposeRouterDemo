package com.github.sceneren.featurea.webview

data class WebviewState(
    val shareInfo: ShareInfo? = null,
    val showShareDialog: Boolean = false,
    val webviewPageTitle: String? = null,
) {
    val pageTitle: String
        get() = webviewPageTitle ?: ""

    val showShareIcon: Boolean
        get() = shareInfo != null && shareInfo.shareUrl != null
}

sealed class WebviewEffect {
    data class ShowToast(val message: String) : WebviewEffect()
}