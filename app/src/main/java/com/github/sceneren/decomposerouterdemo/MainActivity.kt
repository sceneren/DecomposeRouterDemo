package com.github.sceneren.decomposerouterdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.view.WindowCompat
import com.arkivanov.decompose.DecomposeExperimentFlags
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.replaceAll
import com.github.sceneren.common.route.LocalStackRouter
import com.github.sceneren.common.route.MainStackScreens
import com.github.sceneren.decomposerouterdemo.ui.theme.DecomposeRouterDemoTheme
import com.github.sceneren.featurea.camera.CameraScreen
import com.github.sceneren.login.LoginScreen
import io.github.xxfast.decompose.router.LocalRouterContext
import io.github.xxfast.decompose.router.RouterContext
import io.github.xxfast.decompose.router.defaultRouterContext
import io.github.xxfast.decompose.router.stack.RoutedContent
import io.github.xxfast.decompose.router.stack.Router
import io.github.xxfast.decompose.router.stack.rememberRouter

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalDecomposeApi::class)
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

                            RoutedContent(
                                router = router,
                                animation = stackAnimation { child ->
                                    when (child.configuration) {
                                        MainStackScreens.Splash -> fade()
                                        MainStackScreens.Main -> fade()
                                        MainStackScreens.Login -> slide(orientation = Orientation.Vertical)
                                        else -> slide()
                                    }
                                }
                            ) { screen ->
                                when (screen) {
                                    MainStackScreens.Splash -> SplashScreen(
                                        router = router,
                                        onEnterMainScreen = {
                                            router.replaceAll(MainStackScreens.Main)
                                        }
                                    )

                                    MainStackScreens.Main -> MainScreen()
                                    MainStackScreens.Detail -> DetailScreen()
                                    MainStackScreens.Login -> LoginScreen()
                                    MainStackScreens.Camera -> CameraScreen()
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}