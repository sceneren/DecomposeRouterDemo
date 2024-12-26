package com.github.sceneren.featureb.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.lifecycle.compose.LifecycleStartEffect
import com.arkivanov.decompose.router.stack.pushNew
import com.github.sceneren.common.route.LocalStackRouter
import com.github.sceneren.common.route.MainStackScreens
import io.github.xxfast.decompose.router.rememberOnRoute

@Composable
fun FeatureBHomeScreen() {

    val router = LocalStackRouter.current
    LifecycleStartEffect(router) {
        Log.e("FeatureBHomeScreen", "FeatureBHomeScreen==>Start")
        onStopOrDispose { }
    }

    val listState = rememberOnRoute {
        LazyListState()
    }


    Scaffold {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = it.calculateTopPadding(),
                    start = it.calculateStartPadding(LocalLayoutDirection.current),
                    end = it.calculateStartPadding(LocalLayoutDirection.current)
                ),
            state = listState
        ) {
            items(40) { index ->

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.Blue)
                        .clickable {
                            router.pushNew(MainStackScreens.Detail)
                        }
                        .padding(vertical = 10.dp),
                    text = "item->${index}"
                )

            }
        }
    }
}