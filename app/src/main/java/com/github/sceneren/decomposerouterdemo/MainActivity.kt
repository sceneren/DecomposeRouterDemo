package com.github.sceneren.decomposerouterdemo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.core.view.WindowCompat
import com.arkivanov.decompose.DecomposeExperimentFlags
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.animation.Direction
import com.arkivanov.decompose.extensions.compose.stack.animation.StackAnimator
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimator
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.replaceAll
import com.github.sceneren.common.route.LocalStackRouter
import com.github.sceneren.common.route.MainStackScreens
import com.github.sceneren.decomposerouterdemo.ui.theme.DecomposeRouterDemoTheme
import com.github.sceneren.featurea.camera.CameraScreen
import com.github.sceneren.featurea.camposer.CamposerScreen
import com.github.sceneren.featurea.ksoup.KsoupScreen
import com.github.sceneren.featurea.webview.WebviewScreen
import com.github.sceneren.login.LoginScreen
import io.github.xxfast.decompose.router.LocalRouterContext
import io.github.xxfast.decompose.router.RouterContext
import io.github.xxfast.decompose.router.defaultRouterContext
import io.github.xxfast.decompose.router.stack.RoutedContent
import io.github.xxfast.decompose.router.stack.Router
import io.github.xxfast.decompose.router.stack.rememberRouter


class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalSharedTransitionApi::class, ExperimentalDecomposeApi::class)
    @androidx.annotation.RequiresPermission(android.Manifest.permission.RECORD_AUDIO)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        enableEdgeToEdge()
        val rootRouterContext: RouterContext = defaultRouterContext()
        setContent {
            Surface {


                DecomposeExperimentFlags.duplicateConfigurationsEnabled = true

                CompositionLocalProvider(
                    LocalRouterContext provides rootRouterContext,
                ) {
                    val router: Router<MainStackScreens> =
                        rememberRouter { listOf(MainStackScreens.Splash) }
                    CompositionLocalProvider(
                        LocalStackRouter provides router
                    ) {
                        DecomposeRouterDemoTheme {
                            SharedTransitionLayout {
                                RoutedContent(
                                    router = router,
                                    animation = predictiveBackAnimation(
                                        backHandler = LocalRouterContext.current.backHandler,
                                        fallbackAnimation = stackAnimation { child ->
                                            when (child.configuration) {
                                                MainStackScreens.Splash -> fade()
                                                MainStackScreens.Main -> fade()
                                                is MainStackScreens.Login -> {
                                                    loginScreenAnimator()
                                                }

                                                else -> slide()
                                            }
                                        },
                                        onBack = { router.pop() }
                                    )
                                ) @androidx.annotation.RequiresPermission(android.Manifest.permission.RECORD_AUDIO) { screen ->
                                    Log.e(
                                        "MainActivity",
                                        "MainStackScreens==>$screen==>${screen.hashCode()}"
                                    )
                                    when (screen) {
                                        MainStackScreens.Splash -> SplashScreen(
                                            router = router,
                                            onEnterMainScreen = {
                                                router.replaceAll(MainStackScreens.Main)
                                            }
                                        )

                                        MainStackScreens.Main -> MainScreen()

                                        MainStackScreens.Detail -> DetailScreen()

                                        is MainStackScreens.Login -> LoginScreen()
                                        MainStackScreens.Camera -> {
                                            Log.e("MainActivity", "MainStackScreens.Camera")
                                            CameraScreen()
                                        }

                                        MainStackScreens.Camposer -> CamposerScreen()

                                        MainStackScreens.Ksoup -> KsoupScreen()

                                        MainStackScreens.Webview -> WebviewScreen()

                                    }
                                }
                            }

                        }
                    }
                }
            }

        }
    }
}

fun loginScreenAnimator(
    animationSpec: FiniteAnimationSpec<Float> = tween()
): StackAnimator =
    stackAnimator(animationSpec = animationSpec) { factor, direction, content ->

        val modifier = when (direction) {
            Direction.ENTER_FRONT -> {
                Modifier.offsetYFactor(factor = factor)
            }

            Direction.ENTER_BACK -> {
                Modifier.offsetXFactor(factor = factor)
            }

            Direction.EXIT_FRONT -> {
                Modifier.offsetYFactor(factor = factor)
            }

            Direction.EXIT_BACK -> {
                Modifier.offsetXFactor(factor = factor)
            }
        }

        content(modifier)
    }

private fun Modifier.offsetXFactor(factor: Float): Modifier =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)

        layout(placeable.width, placeable.height) {
            placeable.placeRelative(x = (placeable.width.toFloat() * factor).toInt(), y = 0)
        }
    }

private fun Modifier.offsetYFactor(factor: Float): Modifier =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)

        layout(placeable.width, placeable.height) {
            placeable.placeRelative(x = 0, y = (placeable.height.toFloat() * factor).toInt())
        }
    }
