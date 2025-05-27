package com.github.sceneren.featurea.home

import android.util.Log
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.router.stack.pushNew
import com.github.sceneren.common.route.LocalAnimatedVisibilityScope
import com.github.sceneren.common.route.LocalStackRouter
import com.github.sceneren.common.route.MainStackScreens
import io.github.xxfast.decompose.router.slot.RoutedContent
import io.github.xxfast.decompose.router.slot.Router
import io.github.xxfast.decompose.router.slot.rememberRouter
import kotlinx.serialization.Serializable
import org.orbitmvi.orbit.compose.collectAsState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun FeatureAHomeScreen() {

    val vm: FeatureAVM = viewModel()
    val vmState by vm.collectAsState()

//    val listState = rememberOnRoute {
//        LazyListState()
//    }

    val dialogRouter: Router<DialogScreens> = rememberRouter { null }
    val bottomSheet1Router: Router<BottomSheet1Screens> = rememberRouter { null }
    val bottomSheet2Router: Router<BottomSheet2Screens> = rememberRouter { null }

    val rootRouter = LocalStackRouter.current

//    val animatedVisibilityScope = LocalAnimatedVisibilityScope.current

    LifecycleStartEffect(rootRouter) {
        onStopOrDispose { }
    }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        text = "Slot",
                        modifier = Modifier.testTag("TITLE_BAR")
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = Color.Blue),
                scrollBehavior = scrollBehavior
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = it.calculateTopPadding())
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            state = vmState.lazyListState
        ) {

            item {

                Icon(
                    modifier = Modifier
//                        .sharedBounds(
//                            sharedContentState = rememberSharedContentState(key = "image"),
//                            animatedVisibilityScope = animatedVisibilityScope
//                        )
                        .size(20.dp),
                    imageVector = Icons.Outlined.AccountCircle,
                    tint = Color.Red,
                    contentDescription = null
                )
            }

            item {
                Column {
                    Button(
                        onClick = { dialogRouter.activate(DialogScreens) },
                        modifier = Modifier.testTag("BUTTON_DIALOG")
                    ) {
                        Text("Show Dialog")
                    }

                    Button(
                        onClick = { bottomSheet1Router.activate(BottomSheet1Screens()) },
                        modifier = Modifier.testTag("BUTTON_BOTTOM_SHEET")
                    ) {
                        Text("Show Bottom Sheet")
                    }

                    Button(
                        onClick = {
                            rootRouter.pushNew(MainStackScreens.Login)
                        },
                        modifier = Modifier.testTag("to Login")
                    ) {
                        Text("to Login")
                    }

                    Button(
                        onClick = {
                            rootRouter.pushNew(MainStackScreens.Camera)
                        },
                        modifier = Modifier.testTag("to Camera")
                    ) {
                        Text("to Camera")
                    }

                    Button(
                        onClick = {
                            rootRouter.pushNew(MainStackScreens.Camposer)
                        },
                        modifier = Modifier.testTag("to Camposer")
                    ) {
                        Text("to Camposer")
                    }
                }
            }

            items(40) { index ->

                Text(
                    modifier = Modifier
                        .clickable {
                            rootRouter.pushNew(MainStackScreens.Detail)
                        }
                        .padding(vertical = 10.dp),
                    text = "item->${index}"
                )

            }
        }
    }

    RoutedContent(dialogRouter) { screens ->
        AlertDialog(
            onDismissRequest = { dialogRouter.dismiss() },
            confirmButton = {
                TextButton(onClick = { dialogRouter.dismiss() }) { Text("Ok") }
            },
            title = { Text("Dialog") },
            text = {
                Text(
                    text = screens.toString(),
                    modifier = Modifier.padding(8.dp)
                )
            },
            modifier = Modifier.testTag("DIALOG")
        )
    }
    RoutedContent(bottomSheet1Router) { screen ->
        BottomSheet1Screen(bottomSheet1Router, bottomSheet2Router)
    }

    RoutedContent(bottomSheet2Router) { screen ->
        BottomSheet2Screen(bottomSheet2Router)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheet1Screen(
    bottomSheet1Router: Router<BottomSheet1Screens>,
    bottomSheet2Router: Router<BottomSheet2Screens>
) {

    val rootRouter = LocalStackRouter.current

    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { bottomSheet1Router.dismiss() },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 300.dp)
            .testTag("BOTTOM_SHEET"),
        dragHandle = null,
        shape = RoundedCornerShape(0.dp)
    ) {
        Box(
            modifier = Modifier
        ) {
            Column(
                modifier = Modifier.heightIn(max = 300.dp)
            ) {
                Text(
                    text = "BottomSheet1Screen",
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable {
                            bottomSheet2Router.activate(BottomSheet2Screens())
                        }
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { bottomSheet1Router.dismiss() },
                ) {
                    Text("Close")
                }

                Button(
                    onClick = {
                        bottomSheet1Router.dismiss {
                            rootRouter.pushNew(MainStackScreens.Login)
                        }
                    },
                ) {
                    Text("to login")
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheet2Screen(bottomSheetRouter: Router<BottomSheet2Screens>) {
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { bottomSheetRouter.dismiss() },
        modifier = Modifier
            .heightIn(min = 300.dp)
            .testTag("BOTTOM_SHEET")
    ) {
        Scaffold(
            modifier = Modifier.heightIn(max = 500.dp),
            topBar = { TopAppBar(title = { Text("Bottom Sheet2") }) },
            bottomBar = {
                BottomAppBar {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = { bottomSheetRouter.dismiss() },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Ok")
                    }
                }
            }
        ) { scaffoldPadding ->
            Box(
                modifier = Modifier
                    .heightIn(max = 300.dp)
                    .padding(scaffoldPadding)
            ) {
                Text(
                    text = "BottomSheet2Screen",
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center)
                        .clickable {
                            //bottomSheetRouter.activate(bottomSheet2Router)
                        }
                )
            }
        }
    }
}


@Serializable
object DialogScreens


@Serializable
class BottomSheet1Screens

@Serializable
class BottomSheet2Screens

@Serializable
sealed class BottomSheetScreens {
    @Serializable
    data object BottomSheet1 : BottomSheetScreens()

    @Serializable
    data object BottomSheet2 : BottomSheetScreens()
}
