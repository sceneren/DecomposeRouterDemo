package com.github.sceneren.featurea.ksoup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.network.parseGetRequest
import com.fleeksoft.ksoup.nodes.Document
import com.github.sceneren.featurea.webview.ShareInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

data class KsoupState(
    val shareTitle: String? = null,
    val shareIcon: String? = null,
    val shareDesc: String? = null,
    val shareUrl: String? = null,
)


class KsoupVM : ViewModel(), ContainerHost<KsoupState, Nothing> {
    override val container = container<KsoupState, Nothing>(KsoupState()) {
        parseHtml()
    }


    private fun parseHtml() = intent {

        flow {
            val doc: Document =
                Ksoup.parseGetRequest(url = "https://pc.pigeonfan.com/mall.php/Details/putong_xiangqing/id/1001")

            val shareTitle = doc.selectFirst("meta[name=sharetitle]")?.attr("content")
            val shareUrl = doc.selectFirst("meta[name=shareurl]")?.attr("content")
            val shareDesc = doc.selectFirst("meta[name=sharedescription]")?.attr("content")
            val shareIcon = doc.selectFirst("meta[name=shareIcon]")?.attr("content")

            emit(ShareInfo(shareTitle, shareIcon, shareDesc, shareUrl))
        }.catch {
            emit(ShareInfo("error", "error", "error", "error"))
        }.onEach { shareInfo ->
            reduce {
                state.copy(
                    shareTitle = shareInfo.shareTitle,
                    shareUrl = shareInfo.shareUrl,
                    shareDesc = shareInfo.shareDesc,
                    shareIcon = shareInfo.shareIcon
                )
            }
        }.flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)


    }


}