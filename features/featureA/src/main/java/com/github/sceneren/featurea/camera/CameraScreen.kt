package com.github.sceneren.featurea.camera

import android.app.Activity
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.Log
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.SurfaceRequest
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.createBitmap
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.essenty.lifecycle.doOnPause
import com.arkivanov.essenty.lifecycle.doOnResume
import com.arkivanov.essenty.lifecycle.doOnStart
import com.github.sceneren.featurea.R
import com.github.sceneren.featurea.camera.rotate.RealPhysicalScreenRotationMonitor
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import io.github.xxfast.decompose.router.rememberOnRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.concurrent.getOrSet

@androidx.annotation.RequiresPermission(android.Manifest.permission.RECORD_AUDIO)
@Composable
fun CameraScreen() {
    val context = LocalContext.current
    LifecycleStartEffect("CameraScreen") {
        Log.e("CameraScreen", "CameraScreen")
        val testStr = androidx.core.i18n.MessageFormat.format(
            context = context,
            id = R.string.test,
            mapOf("t1" to "aa")
        )
        Log.e("testStr", testStr)
        onStopOrDispose { }
    }

    val saveableStateHolder = rememberSaveableStateHolder()

    Scaffold(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = it.calculateBottomPadding())
        ) {
            saveableStateHolder.SaveableStateProvider("camera") {
                CameraXViewContent()
            }

        }
    }
}

@androidx.annotation.RequiresPermission(android.Manifest.permission.RECORD_AUDIO)
@OptIn(ExperimentalComposeApi::class, ExperimentalComposeUiApi::class)
@Composable
fun CameraXViewContent() {
    val scope = rememberCoroutineScope()
    val viewModel = rememberOnRoute { CameraXVM().apply { doOnDestroy { onCleared() } } }
    val state by viewModel.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val captureController = rememberCaptureController()

    LockedOrientationScreen()

    LaunchedEffect(lifecycleOwner) {
        flow {
            while (true) {
                val bitmapAsync = captureController.captureAsync()
                val realBitmap = bitmapAsync.await().asAndroidBitmap()
                //将硬件加速的位图转换为普通位图
                emit(realBitmap.convertToSoftwareBitmap(3060))
                delay(1000/25)
            }
        }.onEach {
            viewModel.setWatermarkBitmap(it)
        }.flowOn(Dispatchers.IO).launchIn(this)
    }

//    LaunchedEffect(lifecycleOwner) {
//        viewModel.bindToCamera(context.applicationContext, lifecycleOwner)
//    }

    rememberOnRoute {
        doOnResume {
            scope.launch {
                viewModel.bindToCamera(context.applicationContext, lifecycleOwner)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(9f / 16f)
    ) {
        state.surfaceRequest?.let {
            CameraView2(
                modifier = Modifier.fillMaxSize(),
                surfaceRequest = it
            )
        }

        //拍照的角度和设备的角度不知道为什么差90度
        WatermarkView(
            modifier = Modifier.capturable(captureController),
            angle = state.deviceRotation + 90f,
            content = { WatermarkContent(time = state.currentTime) }
        )

        RealPhysicalScreenRotationMonitor {
            viewModel.setDeviceRotation(it)
        }

        Row(modifier = Modifier.align(Alignment.BottomCenter)) {
            Button(onClick = { viewModel.switchRecord(context = context) }) {
                Text(text = if (state.isRecording) "停止录制" else "开始录制")
            }
            Button(onClick = { viewModel.takePhoto() }) {
                Text("拍照")
            }
        }

    }


}

@Composable
fun CameraView2(modifier: Modifier = Modifier, surfaceRequest: SurfaceRequest) {
    CameraXViewfinder(
        modifier = modifier,
        surfaceRequest = surfaceRequest,
    )
}

/**
 * 水印
 * @param angle 水印角度
 * @param content 水印内容
 */
@Composable
fun WatermarkView(
    modifier: Modifier = Modifier,
    angle: Float,
    content: @Composable BoxScope.() -> Unit
) {

    val watermarkAlignment = getWatermarkAlignment(angle)

    var waterMarkSize by remember { mutableStateOf(IntSize(0, 0)) }

    val watermarkOffset = getWaterMarkOffset(
        angle = angle,
        waterMarkSize = waterMarkSize,
        horizontalPadding = 20.dp,
        verticalPadding = 20.dp
    )

    AnimatedContent(
        modifier = modifier,
        targetState = Triple(angle, watermarkAlignment, watermarkOffset)
    ) { triple ->
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .graphicsLayer { applyRotation(triple.first) }
                    .absoluteOffset(offset = { triple.third })
                    .align(alignment = triple.second)
                    .onGloballyPositioned { waterMarkSize = it.size },
                content = content
            )
        }
    }

}

@Composable
fun BoxScope.WatermarkContent(time: String) {
    Column {
        Text(text = "水印时间：${time}", color = Color.Red)
        Text(text = "地址：重庆市XX区XX路XX号", color = Color.Red)
    }

}

/**
 * 旋转水印
 * @param degrees 旋转角度
 */
private fun GraphicsLayerScope.applyRotation(degrees: Float) {
    transformOrigin = TransformOrigin(0f, 0f)
    rotationZ = if (degrees % 180 == 0f) {
        degrees + 90
    } else {
        degrees - 90
    }.toFloat()
}

/**
 * 获取水印对齐方式
 * @param angle 水印角度
 */
private fun getWatermarkAlignment(angle: Float): Alignment {
    return when (angle - 90) {
        0F -> Alignment.BottomStart
        90F -> Alignment.BottomEnd
        180F -> Alignment.TopEnd
        270F -> Alignment.TopStart
        else -> Alignment.TopStart
    }
}

/**
 * 获取水印偏移量
 * @param angle 水印角度
 * @param waterMarkSize 水印大小
 * @param horizontalPadding 水印水平边距
 * @param verticalPadding 水印竖直边距
 */
@Composable
private fun getWaterMarkOffset(
    angle: Float,
    waterMarkSize: IntSize,
    horizontalPadding: Dp = 0.dp,
    verticalPadding: Dp = 0.dp
): IntOffset {

    val widthPaddingPx = LocalDensity.current.run { horizontalPadding.toPx().toInt() }
    val heightPaddingPx = LocalDensity.current.run { verticalPadding.toPx().toInt() }

    return when (angle - 90) {
        0F -> {
            //水平方向X加表示离边远，y减表示离边远
            IntOffset(widthPaddingPx, -heightPaddingPx)
        }

        90F -> {
            //垂直方向X加表示离边远，y减表示离边远
            IntOffset(
                -waterMarkSize.height + widthPaddingPx,
                waterMarkSize.width - waterMarkSize.height - heightPaddingPx
            )
        }

        180F -> {
            //垂直方向X加表示离边远，y减表示离边远
            IntOffset(
                -waterMarkSize.width + widthPaddingPx,
                -waterMarkSize.height - heightPaddingPx
            )
        }

        else -> {
            IntOffset(
                0 + widthPaddingPx,
                -waterMarkSize.width / 2 + waterMarkSize.height - heightPaddingPx
            )
        }
    }
}


@Composable
fun LockedOrientationScreen() {
    val context = LocalContext.current
    val activity = context as? Activity // Cast context to Activity

    // Use DisposableEffect to manage the orientation
    DisposableEffect(activity) {
        val originalOrientation = activity?.requestedOrientation // Store original orientation

        // Lock the screen to portrait when this Composable is active
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // When the Composable leaves the composition, revert to original orientation
        onDispose {
            originalOrientation?.let {
                activity.requestedOrientation = it
            }
        }
    }
}


private val dateFormatThreadLocal = ThreadLocal.withInitial {
    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
}

fun Long.timestampToFormattedDate(): String {
    val sdf = dateFormatThreadLocal.getOrSet {
        SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.getDefault()
        )
    }
    return sdf.format(Date(this))
}

fun Bitmap.convertToSoftwareBitmap(targetWidth: Int): Bitmap {
    val tmpBitmap = this.copy(Bitmap.Config.ARGB_8888, true)

    // 计算缩放比例
    val scaleFactor = targetWidth.toFloat() / tmpBitmap.width.toFloat()

    // 计算目标高度，保持宽高比
    val scaledHeight = (tmpBitmap.height * scaleFactor).toInt()

    // 创建一个目标大小的空白位图
    val scaledBitmap = createBitmap(targetWidth, scaledHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(scaledBitmap)

    // 设置缩放矩阵
    val matrix = Matrix()
    matrix.postScale(scaleFactor, scaleFactor)

    // 绘制缩放后的位图到画布上
    canvas.drawBitmap(tmpBitmap, matrix, null)

    // 回收临时使用的位图以释放内存
    tmpBitmap.recycle()

    return scaledBitmap
}