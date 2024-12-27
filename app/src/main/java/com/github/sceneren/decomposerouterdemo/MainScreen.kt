package com.github.sceneren.decomposerouterdemo

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.router.pages.selectFirst
import com.github.sceneren.common.composable.BarShape
import com.github.sceneren.common.composable.ConvexBottomBar
import com.github.sceneren.common.route.MainPagerScreens
import com.github.sceneren.featurea.home.FeatureAHomeScreen
import com.github.sceneren.featureb.home.FeatureBHomeScreen
import com.github.sceneren.featurec.home.FeatureCHomeScreen
import io.github.xxfast.decompose.router.pages.RoutedContent
import io.github.xxfast.decompose.router.pages.Router
import io.github.xxfast.decompose.router.pages.pagesOf
import io.github.xxfast.decompose.router.pages.rememberRouter

@Composable
fun MainScreen() {
    val pager: Router<MainPagerScreens> = rememberRouter {
        pagesOf(
            MainPagerScreens.Page1,
            MainPagerScreens.Page2,
            MainPagerScreens.Page3
        )
    }

    BackHandler(pager.pages.value.selectedIndex != 0) {
        pager.selectFirst()
    }

    Scaffold(
        bottomBar = {
            ConvexBottomBar(
                modifier = Modifier
                    .navigationBarsPadding()
                    .fillMaxWidth(),
                backGroundColor = Color.Blue
            ) {

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .size(22.dp),
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                    )
                    Text(
                        text = "标题1",
                        fontSize = 12.sp
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        modifier = Modifier.size(40.dp),
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                    )
                    Text(
                        text = "标题2",
                        fontSize = 12.sp
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .size(22.dp),
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                    )
                    Text(
                        text = "标题3",
                        fontSize = 12.sp
                    )
                }

            }
        }
    ) { innerPadding ->
        RoutedContent(
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
            router = pager,
            pager = { modifier, state, key, pageContent ->
                HorizontalPager(
                    modifier = modifier,
                    state = state,
                    key = key,
                    pageContent = pageContent,
                    userScrollEnabled = false,
                    beyondViewportPageCount = 0
                )
            }
        ) { page ->
            when (page) {
                MainPagerScreens.Page1 -> FeatureAHomeScreen()
                MainPagerScreens.Page2 -> FeatureBHomeScreen()
                MainPagerScreens.Page3 -> FeatureCHomeScreen()
            }
        }
    }
}