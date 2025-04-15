package com.github.sceneren.featurec.home

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

data class FeatureCState(
    val searchText: String = "",
    val searchExpand: Boolean = false,
    val searchResultList: PersistentList<String> = persistentListOf(),
    val number1: Int = 0,
    val number2: Int = 0,
    val result: Int = 0,
    val appKey: String? = null
)

sealed class FeatureCEffect {
    data object Test : FeatureCEffect()
}