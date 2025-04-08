package com.github.sceneren.decomposerouterdemo

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.sceneren.common.route.LocalAnimatedVisibilityScope

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.DetailScreen() {
    val animatedVisibilityScope = LocalAnimatedVisibilityScope.current
    Scaffold { innerPadding ->

        Column {
            Icon(
                modifier = Modifier
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(key = "image1"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                    .align(Alignment.CenterHorizontally)
                    .size(60.dp),
                imageVector = Icons.Outlined.AccountCircle,
                tint = Color.Red,
                contentDescription = null
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.clickable {

                    },
                    text = "这是详情"
                )
            }
        }


    }
}