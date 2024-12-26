package com.github.sceneren.featurea.home

import androidx.compose.foundation.lazy.LazyListState

data class FeatureAState(
    val lazyListState: LazyListState = LazyListState()
//    val id: String = ""
)

sealed class FeatureAEffect{
    data object Test:FeatureAEffect()
}