package com.github.sceneren.featurec.home

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import com.github.sceneren.common.route.LocalStackRouter
import io.github.xxfast.decompose.router.rememberOnRoute
import org.orbitmvi.orbit.compose.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeatureCHomeScreen() {
    val router = LocalStackRouter.current
    LifecycleStartEffect(router) {
        Log.e("FeatureCHomeScreen", "FeatureCHomeScreen==>Start")
        onStopOrDispose { }
    }

    val vm = rememberOnRoute { FeatureCVM() }

    val vmState by vm.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            modifier = Modifier.semantics { traversalIndex = 0f },
            inputField = {
                SearchBarDefaults.InputField(
                    query = vmState.searchText,
                    onQueryChange = { vm.changeSearchText(it) },
                    onSearch = { vm.changeSearchExpand(false) },
                    expanded = vmState.searchExpand,
                    onExpandedChange = { vm.changeSearchExpand(it) },
                    placeholder = { Text("Hinted search text") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = { Icon(Icons.Default.MoreVert, contentDescription = null) },
                )
            },
            expanded = vmState.searchExpand,
            onExpandedChange = {
                vm.changeSearchExpand(it)
            },
            content = {
                vmState.searchResultList.forEach { resultText ->
                    ListItem(
                        headlineContent = { Text(resultText) },
                        supportingContent = { Text("Additional info") },
                        leadingContent = { Icon(Icons.Filled.Star, contentDescription = null) },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        modifier = Modifier
                            .clickable {
                                vm.changeSearchText(resultText)
                                vm.changeSearchExpand(false)
                            }
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp))
                }

            }
        )

        TextField(
            value = vmState.number1.toString(),
            onValueChange = { vm.setNumber1(it.toIntOrNull() ?: 0) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        TextField(
            value = vmState.number2.toString(),
            onValueChange = { vm.setNumber2(it.toIntOrNull() ?: 0) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Button(onClick = { vm.calculate() }) {
            Text(text = "计算2个数的和")
        }
        Text(text = "这2个数的和是：${vmState.result}")

        Text(text = "从rust获取到的appKey是：${vmState.appKey}")
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "FeatureCHomeScreen")
    }
}