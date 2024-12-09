package com.github.sceneren.decomposerouterdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.view.WindowCompat
import com.arkivanov.decompose.router.stack.pushToFront
import com.arkivanov.decompose.router.stack.replaceAll
import com.github.sceneren.common.route.MainStackScreens
import com.github.sceneren.decomposerouterdemo.ui.theme.DecomposeRouterDemoTheme
import io.github.xxfast.decompose.router.LocalRouterContext
import io.github.xxfast.decompose.router.RouterContext
import io.github.xxfast.decompose.router.defaultRouterContext
import io.github.xxfast.decompose.router.stack.RoutedContent
import io.github.xxfast.decompose.router.stack.Router
import io.github.xxfast.decompose.router.stack.rememberRouter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        val rootRouterContext: RouterContext = defaultRouterContext()
        setContent {
            Surface {
                CompositionLocalProvider(LocalRouterContext provides rootRouterContext) {

                    val router: Router<MainStackScreens> =
                        rememberRouter { listOf(MainStackScreens.Splash) }

                    DecomposeRouterDemoTheme {
                        RoutedContent(router = router) { screen ->
                            when (screen) {
                                MainStackScreens.Splash -> SplashScreen(
                                    router = router,
                                    onEnterMainScreen = {
                                        router.replaceAll(MainStackScreens.Main)
                                    }
                                )

                                MainStackScreens.Main -> MainScreen()
                                MainStackScreens.Detail -> DetailScreen()
                            }
                        }
                    }
                }
            }

        }
    }
}
