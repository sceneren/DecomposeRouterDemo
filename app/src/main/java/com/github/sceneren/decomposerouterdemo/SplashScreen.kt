package com.github.sceneren.decomposerouterdemo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.replaceAll
import com.github.sceneren.common.route.MainStackScreens
import io.github.xxfast.decompose.router.stack.Router

@Composable
fun SplashScreen(
    router: Router<MainStackScreens>,
    onEnterMainScreen: () -> Unit
) {

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier.clickable {
                    //onEnterMainScreen()
                    router.replaceAll(MainStackScreens.Main)
                },
                text = "这是启动页"
            )
        }
    }
}