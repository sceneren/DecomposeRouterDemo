package com.github.sceneren.common.route

import kotlinx.serialization.Serializable

interface NeedLogin

@Serializable
sealed class MainStackScreens {
    @Serializable
    data object Splash : MainStackScreens()

    @Serializable
    data object Main : MainStackScreens()

    @Serializable
    data class Login(val loginSuccessRoute: MainStackScreens? = null) : MainStackScreens()

    @Serializable
    data object Detail : MainStackScreens()

    @Serializable
    data object Camera : MainStackScreens()

    @Serializable
    data object Camposer : MainStackScreens()

    @Serializable
    data object Ksoup : MainStackScreens()

}