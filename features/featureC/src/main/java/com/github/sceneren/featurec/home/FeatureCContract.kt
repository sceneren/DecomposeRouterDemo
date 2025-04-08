package com.github.sceneren.featurec.home

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

data class FeatureCState(
    val searchText: String = "",
    val searchExpand: Boolean = false,
    val searchResultList:PersistentList<String> = persistentListOf(),
)

sealed class FeatureCEffect {
    data object Test : FeatureCEffect()
}