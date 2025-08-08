package com.github.sceneren.featurea.ksoup

import androidx.lifecycle.ViewModel
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.model.MetaData
import com.fleeksoft.ksoup.network.parseGetRequest
import com.fleeksoft.ksoup.nodes.Document
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
        val doc: Document =
            Ksoup.parseGetRequest(url = "https://pc.pigeonfan.com/mall.php/Details/putong_xiangqing/id/1001")

        val shareTitle = doc.selectFirst("meta[name=sharetitle]")?.attr("content")
        val shareUrl = doc.selectFirst("meta[name=shareurl]")?.attr("content")
        val shareDesc = doc.selectFirst("meta[name=sharedescription]")?.attr("content")
        val shareIcon = doc.selectFirst("meta[name=shareIcon]")?.attr("content")
        reduce {
            state.copy(
                shareTitle = shareTitle,
                shareUrl = shareUrl,
                shareDesc = shareDesc,
                shareIcon = shareIcon
            )
        }
    }


}