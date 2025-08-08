package com.github.sceneren.featurea.webview

import com.github.sceneren.common.viewmodel.BaseVM

class WebviewVM : BaseVM<WebviewState, Nothing>(WebviewState()) {

    fun setShareInfo(shareInfo: ShareInfo?) = intent {

        reduce {
            state.copy(shareInfo = shareInfo)
        }

    }

    fun setPageTitle(pageTitle: String?) = intent {
        reduce {
            state.copy(webviewPageTitle = pageTitle)
        }
    }

    fun showShareDialog() = intent {
        reduce {
            state.copy(showShareDialog = true)
        }
    }

    fun dismissShareDialog() = intent {
        reduce {
            state.copy(showShareDialog = false)
        }
    }

}