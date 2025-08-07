package com.github.sceneren.decomposerouterdemo

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.router.pages.select
import com.arkivanov.decompose.router.pages.selectFirst
import com.arkivanov.decompose.router.pages.selectLast
import com.github.sceneren.common.composable.ConvexBottomBar
import com.github.sceneren.common.route.MainPagerScreens
import com.github.sceneren.featurea.home.FeatureAHomeScreen
import com.github.sceneren.featurea.home.FeatureAVM
import com.github.sceneren.featureb.home.FeatureBHomeScreen
import com.github.sceneren.featurec.home.FeatureCHomeScreen
import io.github.xxfast.decompose.router.pages.RoutedContent
import io.github.xxfast.decompose.router.pages.Router
import io.github.xxfast.decompose.router.pages.pagesOf
import io.github.xxfast.decompose.router.pages.rememberRouter

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.MainScreen() {

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
                backgroundColor = Color.Blue,
                borderColor = Color.Red,
                borderWidth = 0.5.dp
            ) {

                Column(
                    modifier = Modifier
                        .clickable {
                            pager.selectFirst()
                        }
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
                        .clickable {
                            pager.select(1)
                        }
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
                        .clickable {
                            pager.selectLast()
                        }
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