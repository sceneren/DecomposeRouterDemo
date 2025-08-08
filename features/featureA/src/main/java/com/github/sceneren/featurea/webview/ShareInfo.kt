package com.github.sceneren.featurea.webview

import kotlinx.serialization.Serializable

@Serializable
data class ShareInfo(
    val shareTitle: String? = null,
    val shareIcon: String? = null,
    val shareDesc: String? = null,
    val shareUrl: String? = null
)