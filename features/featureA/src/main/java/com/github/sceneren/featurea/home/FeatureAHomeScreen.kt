package com.github.sceneren.featurea.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.sceneren.common.route.MainPagerScreens
import com.github.sceneren.common.route.MainStackScreens
import io.github.xxfast.decompose.router.stack.Router

@Composable
fun FeatureAHomeScreen(
    mainRouter: Router<MainStackScreens>,
    mainPager: io.github.xxfast.decompose.router.pages.Router<MainPagerScreens>
) {

    Surface {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "FeatureAHomeScreen")
        }
    }

//    Box(
//        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        Text(text = "FeatureAHomeScreen")
//    }
}
