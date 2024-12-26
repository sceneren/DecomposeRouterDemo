package com.github.sceneren.common.route

import kotlinx.serialization.Serializable

@Serializable
sealed class MainStackScreens {
    @Serializable
    data object Splash : MainStackScreens()

    @Serializable
    data object Main : MainStackScreens()

    @Serializable
    data object Login : MainStackScreens()

    @Serializable
    data object Detail : MainStackScreens()

}