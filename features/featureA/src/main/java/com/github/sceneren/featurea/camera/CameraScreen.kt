package com.github.sceneren.featurea.camera

import android.graphics.Canvas
import android.icu.text.MessageFormat
import android.os.Environment
import android.util.Log
import android.view.ViewGroup
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.AspectRatio
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.sceneren.featurea.R
import com.otaliastudios.cameraview.CameraException
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.VideoResult
import com.otaliastudios.cameraview.controls.Mode
import com.otaliastudios.cameraview.overlay.OverlayLayout
import io.github.xxfast.decompose.router.rememberOnRoute
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.compose.collectAsState
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.concurrent.getOrSet

@Preview
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

@Composable
fun CameraXViewContent() {
    val viewModel = viewModel<CameraXVM>()
    val state by viewModel.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val textSize = LocalDensity.current.run { 20.sp.toPx() }
    LaunchedEffect(textSize) {
        viewModel.setTextSize(textSize)
    }

    LaunchedEffect(lifecycleOwner) {
        viewModel.bindToCamera(context.applicationContext, lifecycleOwner)
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

        Text(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 20.dp),
            text = "Watermark ${System.currentTimeMillis()}",
            color = Color.Red,
            fontSize = 20.sp
        )

        Row(modifier = Modifier.align(Alignment.BottomCenter)) {
            Button(onClick = {}) {
                Text("开始录制")
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


@Composable
fun CameraView(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    var currentTime by remember {
        mutableStateOf("")
    }

    var isRecording by remember {
        mutableStateOf(false)
    }

    var angles by remember {
        mutableIntStateOf(90)
    }

    val waterMarkView = rememberOnRoute {
        ComposeView(context).apply {
            setContent {
                WatermarkView(
                    angle = angles,
                    content = { WatermarkContent(time = currentTime) })
            }
        }
    }

    val cameraView = remember {
        CameraView(context).apply {
            Log.e("CameraView", "创建相机")
            keepScreenOn = true
            mode = Mode.VIDEO
            //engine = Engine.CAMERA1
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
            previewFrameRate = 0f

            mode = Mode.PICTURE

            val params = OverlayLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                drawHardwareOverlays = true
                drawOnPreview = true
                drawOnVideoSnapshot = true
                drawOnPictureSnapshot = true
            }

            addView(waterMarkView, params)
            addCameraListener(object : CameraListener() {
                override fun onCameraError(exception: CameraException) {
                    super.onCameraError(exception)
                    destroy()
                    open()
                }

                override fun onVideoTaken(result: VideoResult) {
                    // A Video was taken!
                }

                override fun onVideoRecordingStart() {
                    // Notifies that the actual video recording has started.
                    // Can be used to show some UI indicator for video recording or counting time.
                    isRecording = true
                }

                override fun onVideoRecordingEnd() {
                    // Notifies that the actual video recording has ended.
                    // Can be used to remove UI indicators added in onVideoRecordingStart.
                    isRecording = false
                }
            })
            addFrameProcessor { frame ->
                angles = frame.rotationToUser
                //currentTime = System.currentTimeMillis().toString()
            }
        }
    }

    LaunchedEffect(cameraView) {
        while (true) {
            currentTime = System.currentTimeMillis().toString()
            delay(1000)
        }
    }

    LifecycleResumeEffect(Unit) {
        Log.e("AndroidView", "LifecycleResumeEffect")
        cameraView.open()
        onPauseOrDispose {
            Log.e("AndroidView", "onPauseOrDispose")
            cameraView.close()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            Log.e(
                "AndroidView",
                "DisposableEffect${System.currentTimeMillis().timestampToFormattedDate()}"
            )
            cameraView.destroy()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                cameraView
            },
            update = {

            },
            onReset = {
                cameraView.open()
            },
            onRelease = {
                Log.e("AndroidView", "onRelease")
                //cameraView.destroy()
            })

        Button(
            onClick = {

                if (cameraView.isTakingVideo) {
                    Log.e("结束录制", System.currentTimeMillis().timestampToFormattedDate())
                    cameraView.stopVideo()
                } else {
                    Log.e("开始录制", System.currentTimeMillis().timestampToFormattedDate())

                    val filePath =
                        context.externalCacheDir?.absolutePath + File.separator + "${System.currentTimeMillis()}.mp4"
                    val file = File(filePath)
                    if (!file.exists()) {
                        file.createNewFile()
                    }
                    cameraView.takeVideoSnapshot(file)
                }
            }, modifier = Modifier.testTag("BUTTON_DIALOG")
        ) {
            if (isRecording) {

                Text("结束录制")
            } else {

                Text("开始录制")
            }

        }

    }


}

/**
 * 水印
 * @param angle 水印角度
 * @param content 水印内容
 */
@Composable
fun WatermarkView(angle: Int, content: @Composable BoxScope.() -> Unit) {

    val watermarkAlignment = getWatermarkAlignment(angle)

    var waterMarkSize by remember { mutableStateOf(IntSize(0, 0)) }

    val watermarkOffset = getWaterMarkOffset(
        angle = angle,
        waterMarkSize = waterMarkSize,
        horizontalPadding = 20.dp,
        verticalPadding = 20.dp
    )

    AnimatedContent(Triple(angle, watermarkAlignment, watermarkOffset)) { triple ->
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
private fun GraphicsLayerScope.applyRotation(degrees: Int) {
    transformOrigin = TransformOrigin(0f, 0f)
    rotationZ = if (degrees % 180 == 0) {
        degrees + 90
    } else {
        degrees - 90
    }.toFloat()
}

/**
 * 获取水印对齐方式
 * @param angle 水印角度
 */
private fun getWatermarkAlignment(angle: Int): Alignment {
    return when (angle - 90) {
        0 -> Alignment.BottomStart
        90 -> Alignment.BottomEnd
        180 -> Alignment.TopEnd
        270 -> Alignment.TopStart
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
    angle: Int,
    waterMarkSize: IntSize,
    horizontalPadding: Dp = 0.dp,
    verticalPadding: Dp = 0.dp
): IntOffset {

    val widthPaddingPx = LocalDensity.current.run { horizontalPadding.toPx().toInt() }
    val heightPaddingPx = LocalDensity.current.run { verticalPadding.toPx().toInt() }

    return when (angle - 90) {
        0 -> {
            //水平方向X加表示离边远，y减表示离边远
            IntOffset(widthPaddingPx, -heightPaddingPx)
        }

        90 -> {
            //垂直方向X加表示离边远，y减表示离边远
            IntOffset(
                -waterMarkSize.height + widthPaddingPx,
                waterMarkSize.width - waterMarkSize.height - heightPaddingPx
            )
        }

        180 -> {
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