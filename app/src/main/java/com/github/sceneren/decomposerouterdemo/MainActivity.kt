package com.github.sceneren.decomposerouterdemo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.view.WindowCompat
import com.arkivanov.decompose.DecomposeExperimentFlags
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.replaceAll
import com.github.sceneren.common.route.LocalStackRouter
import com.github.sceneren.common.route.MainStackScreens
import com.github.sceneren.common.route.NeedLogin
import com.github.sceneren.decomposerouterdemo.ui.theme.DecomposeRouterDemoTheme
import com.github.sceneren.featurea.camera.CameraScreen
import com.github.sceneren.featurea.camposer.CamposerScreen
import com.github.sceneren.featurea.ksoup.KsoupScreen
import com.github.sceneren.login.LoginScreen
import io.github.xxfast.decompose.router.LocalRouterContext
import io.github.xxfast.decompose.router.RouterContext
import io.github.xxfast.decompose.router.defaultRouterContext
import io.github.xxfast.decompose.router.stack.RoutedContent
import io.github.xxfast.decompose.router.stack.Router
import io.github.xxfast.decompose.router.stack.rememberRouter


class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalDecomposeApi::class, ExperimentalSharedTransitionApi::class)
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
//                                ChildStack(
//                                    stack = router.stack.value,
//                                    animation = com.arkivanov.decompose.extensions.compose.experimental.stack.animation.stackAnimation(
//                                        animator = com.arkivanov.decompose.extensions.compose.experimental.stack.animation.fade(),
//                                    )
//                                ) {
//                                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                                RoutedContent(
                                    router = router,
                                    animation = stackAnimation { child ->
                                        when (child.configuration) {
                                            MainStackScreens.Splash -> fade()
                                            MainStackScreens.Main -> fade()
                                            is MainStackScreens.Login -> slide(orientation = Orientation.Vertical)
                                            else -> slide()
                                        }
                                    }
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
                                    }
                                }
                            }

                        }


//                            }
//                        }
                    }
                }
            }

        }
    }
}