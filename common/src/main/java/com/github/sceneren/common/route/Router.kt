package com.github.sceneren.common.route

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import io.github.xxfast.decompose.router.stack.Router

val LocalStackRouter: ProvidableCompositionLocal<Router<MainStackScreens>> =   staticCompositionLocalOf { error("Root RouterContext was not provided") }
val LocalAnimatedVisibilityScope: ProvidableCompositionLocal<AnimatedVisibilityScope> =   staticCompositionLocalOf { error("AnimatedVisibilityScope was not provided") }