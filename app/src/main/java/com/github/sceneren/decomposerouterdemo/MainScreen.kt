package com.github.sceneren.decomposerouterdemo

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.router.pages.select
import com.arkivanov.decompose.router.pages.selectFirst
import com.github.sceneren.common.route.MainPagerScreens
import com.github.sceneren.featurea.home.FeatureAHomeScreen
import com.github.sceneren.featureb.home.FeatureBHomeScreen
import com.github.sceneren.featurec.home.FeatureCHomeScreen
import io.github.xxfast.decompose.router.pages.RoutedContent
import io.github.xxfast.decompose.router.pages.Router
import io.github.xxfast.decompose.router.pages.pagesOf
import io.github.xxfast.decompose.router.pages.rememberRouter
import io.github.xxfast.decompose.router.rememberOnRoute

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
            NavigationBar {
                MainPagerScreens.entries.forEach { screen ->
                    NavigationBarItem(
                        selected = screen.ordinal == pager.pages.value.selectedIndex,
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                            )
                        },
                        onClick = { pager.select(screen.ordinal) },
                        label = {
                            Text(screen.title)
                        },
                        alwaysShowLabel = true
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