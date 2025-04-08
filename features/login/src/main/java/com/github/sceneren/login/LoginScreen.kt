package com.github.sceneren.login

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.router.stack.pushNew
import com.github.sceneren.common.route.LocalAnimatedVisibilityScope
import com.github.sceneren.common.route.LocalStackRouter
import com.github.sceneren.common.route.MainStackScreens

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.LoginScreen() {
    val rootRouter = LocalStackRouter.current
    val animatedVisibilityScope = LocalAnimatedVisibilityScope.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier
                .padding(top = 100.dp)
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(key = "image"),
                    animatedVisibilityScope = animatedVisibilityScope,
                )

                .size(20.dp)
                .clickable {
                    rootRouter.pushNew(MainStackScreens.Detail)
                },
            imageVector = Icons.Outlined.AccountCircle,
            tint = Color.Red,
            contentDescription = null
        )
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "LoginScreen")
    }
}