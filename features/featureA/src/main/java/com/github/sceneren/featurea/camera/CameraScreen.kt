package com.github.sceneren.featurea.camera

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.Surface
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.otaliastudios.cameraview.CameraException
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.VideoResult
import com.otaliastudios.cameraview.controls.Mode
import com.otaliastudios.cameraview.overlay.OverlayLayout
import io.github.xxfast.decompose.router.rememberOnRoute
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun CameraScreen() {
    Scaffold(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it), contentAlignment = Alignment.BottomCenter
        ) {
            CameraView()
        }


    }
}


@Composable
fun CameraView(modifier: Modifier = Modifier) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    val textView = rememberOnRoute {
        TextView(context).apply {
            setTextColor(Color.Red.toArgb())
        }
    }

    var isRecording by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = isRecording) {
        var count = 0
        while (isRecording) {
            textView.text = (count++).toString() // 发送当前计数
            delay(1000) // 延迟 1 秒
        }
        textView.text = "0"
    }

    val cameraView = rememberOnRoute {
        CameraView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
            val params = OverlayLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                drawOnPreview = true
                drawHardwareOverlays = true
                drawOnVideoSnapshot = true
                drawOnPictureSnapshot = true
            }

            addView(textView, params)
            setLifecycleOwner(lifecycleOwner)

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

        }
    }

    val angle = getScreenRotationAngle()
    Log.e("CameraView", "屏幕旋转角度：$angle")

    Box(
        modifier = modifier.graphicsLayer { rotationX = angle },
        contentAlignment = Alignment.BottomCenter
    ) {
        AndroidView(modifier = Modifier.fillMaxSize(), factory = {
            cameraView
        }, update = {

        }, onReset = {

        }, onRelease = { cameraView ->
            cameraView.destroy()
        })

        Button(
            onClick = {

                if (cameraView.isTakingVideo) {
                    cameraView.stopVideo()
                } else {
                    cameraView.mode = Mode.VIDEO
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

@Composable
fun getScreenRotationAngle(): Float {
    val context = LocalContext.current
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        context.display
    } else {
        windowManager.defaultDisplay
    }

    val rotation = display.rotation
    return when (rotation) {
        Surface.ROTATION_0 -> 0f
        Surface.ROTATION_90 -> 90f
        Surface.ROTATION_180 -> 180f
        Surface.ROTATION_270 -> 270f
        else -> 0f
    }
}