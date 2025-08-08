package com.github.sceneren.featurea.webview

import android.annotation.SuppressLint
import android.webkit.WebView
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.router.stack.pop
import com.elvishew.xlog.XLog
import com.github.sceneren.common.route.LocalStackRouter
import com.github.sceneren.common.viewmodel.rememberVMOnRoute
import com.multiplatform.webview.web.AccompanistWebViewClient
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.PlatformWebViewParams
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import kotlinx.serialization.json.Json
import org.orbitmvi.orbit.compose.collectAsState

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebviewScreen() {
    val router = LocalStackRouter.current

    val vm = rememberVMOnRoute {
        WebviewVM()
    }

    val vmState by vm.collectAsState()

    val webViewClient = remember {
        object : AccompanistWebViewClient() {
            override fun onPageFinished(view: WebView, url: String?) {
                super.onPageFinished(view, url)
                XLog.e("onPageFinished")
                // 使用示例
                val queries = listOf(
                    "shareDesc" to "meta[name=sharedescription]",
                    "shareUrl" to "meta[name=shareurl]",
                    "shareTitle" to "meta[name=sharetitle]",
                    "shareIcon" to "meta[name=shareicon]"
                )
                val js = getCustomMetaTagJs(queries)
                view.evaluateJavascript(js) { value ->
                    runCatching {
                        val json = value?.replace("\\", "")?.removeQuotes()
                        XLog.e("OGImage==$json")
                        if (!json.isNullOrEmpty()) {
                            val shareInfo = Json.decodeFromString<ShareInfo>(json)
                            vm.setShareInfo(shareInfo)
                        }
                    }

                }
            }
        }
    }

    val webviewNavigator = rememberWebViewNavigator()

    val webViewState =
        rememberWebViewState("https://www.baidu.com")



    LaunchedEffect(webViewState.loadingState) {
        when(webViewState.loadingState){
            LoadingState.Finished -> XLog.e("LoadingState.Finished")
            LoadingState.Initializing -> XLog.e("LoadingState.Initializing")
            is LoadingState.Loading -> XLog.e("LoadingState.Loading")
        }
    }

    LaunchedEffect(webViewState.errorsForCurrentRequest) {
        webViewState.errorsForCurrentRequest.forEach {
            XLog.e(it.toString())
        }
    }


    LaunchedEffect(webViewState.pageTitle) {
        vm.setPageTitle(webViewState.pageTitle)
    }

    DisposableEffect(Unit) {
        webViewState.webSettings.apply {
            isJavaScriptEnabled = true
            androidWebSettings.apply {
                isAlgorithmicDarkeningAllowed = true
                safeBrowsingEnabled = true
            }
        }

        onDispose { }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        modifier = Modifier.basicMarquee(),
                        maxLines = 1,
                        text = vmState.pageTitle
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (webviewNavigator.canGoBack) {
                            webviewNavigator.navigateBack()
                        } else {
                            router.pop()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }

                },
                actions = {

                    if (vmState.showShareIcon) {

                        IconButton(onClick = {
                            vm.showShareDialog()
                        }) {
                            Icon(Icons.Filled.Share, contentDescription = "分享")
                        }

                        Spacer(Modifier.width(10.dp))
                    }

                    IconButton(onClick = {
                        router.pop()
                    }) {
                        Icon(Icons.Filled.Close, contentDescription = "关闭")
                    }
                }
            )
        }
    ) { paddingValues ->
        com.multiplatform.webview.web.WebView(
            state = webViewState,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            navigator = webviewNavigator,
            platformWebViewParams = PlatformWebViewParams(
                client = webViewClient,
                chromeClient = null
            )
        )
    }

    if (vmState.showShareDialog) {
        AlertDialog(
            onDismissRequest = { vm.dismissShareDialog() },
            confirmButton = {
                TextButton(onClick = { vm.dismissShareDialog() }) { Text("Ok") }
            },
            title = { Text("分享") },
            text = {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(text = "shareTitle=${vmState.shareInfo?.shareTitle}")
                    Spacer(Modifier.height(10.dp))
                    Text(text = "shareDesc=${vmState.shareInfo?.shareDesc}")
                    Spacer(Modifier.height(10.dp))
                    Text(text = "shareUrl=${vmState.shareInfo?.shareUrl}")
                    Spacer(Modifier.height(10.dp))
                    Text(text = "icon=${vmState.shareInfo?.shareIcon}")
                    Spacer(Modifier.height(10.dp))
                }

            },
            modifier = Modifier.testTag("DIALOG")
        )
    }


}

fun getCustomMetaTagJs(metaQueries: List<Pair<String, String>>): String {
    val jsArray = metaQueries.joinToString(",") { (name, query) ->
        "{name: '$name', query: '$query'}"
    }

    return """
        (function() {
            var result = {};
            var metaTags = [$jsArray];
            
            metaTags.forEach(function(tag) {
                var element = document.querySelector(tag.query);
                result[tag.name] = element ? element.content : null;
            });
            
            return JSON.stringify(result);
        })()
    """.trimIndent()
}

fun String.removeQuotes(): String {
    // 检查字符串长度，以防止空字符串引发异常
    return if (this.length >= 2 && this.startsWith("\"") && this.endsWith("\"")) {
        // 使用substring去掉首尾双引号
        this.substring(1, this.length - 1)
    } else {
        // 如果字符串不以双引号开头或结尾，则返回原始字符串
        this
    }
}