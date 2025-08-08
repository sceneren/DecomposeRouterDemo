package com.github.sceneren.featurea.ksoup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.router.stack.pop
import com.github.sceneren.common.route.LocalStackRouter
import io.github.xxfast.decompose.router.rememberOnRoute
import org.orbitmvi.orbit.compose.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KsoupScreen() {

    val router = LocalStackRouter.current

    val vm = rememberOnRoute {
        KsoupVM()
    }

    val vmState by vm.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Ksoup")
                },
                navigationIcon = {

                    IconButton(onClick = {
                        router.pop()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }

                })
        }
    ) { paddingValues ->
        Column(modifier = Modifier
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())) {

            Text(text = "shareUrl=${vmState.shareUrl}")
            Text(text = "shareIcon=${vmState.shareIcon}")
            Text(text = "shareTitle=${vmState.shareTitle}")
            Text(text = "shareDesc=${vmState.shareDesc}")

        }
    }
}