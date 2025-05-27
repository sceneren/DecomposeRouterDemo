package com.github.sceneren.featurea.camposer

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.camera.core.AspectRatio
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.MediaStoreOutputOptions
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.net.toFile
import androidx.core.os.EnvironmentCompat
import com.elvishew.xlog.XLog
import com.github.sceneren.featurea.camera.timestampToFormattedDate
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ujizin.camposer.CameraPreview
import com.ujizin.camposer.state.CamSelector
import com.ujizin.camposer.state.CaptureMode
import com.ujizin.camposer.state.ImageCaptureResult
import com.ujizin.camposer.state.ImageTargetSize
import com.ujizin.camposer.state.VideoCaptureResult
import com.ujizin.camposer.state.rememberCamSelector
import com.ujizin.camposer.state.rememberCameraState
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CamposerScreen() {

    // 1. 创建并记住多个权限的状态
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO,
        )
    )

    val context = LocalContext.current

    if (permissionsState.allPermissionsGranted) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            CamposerScreenContent()
        }

    } else {
        Scaffold {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                contentAlignment = Alignment.Center
            ) {
                Button(onClick = {
                    permissionsState.launchMultiplePermissionRequest()
                }) {
                    Text(text = "申请权限")
                }
            }
        }
    }


}

@androidx.annotation.RequiresPermission(android.Manifest.permission.RECORD_AUDIO)
@Composable
fun CamposerScreenContent() {
    val cameraState = rememberCameraState()
    var camSelector by rememberCamSelector(CamSelector.Back)
    var zoomRatio by remember { mutableFloatStateOf(cameraState.minZoom) }
    val imageCaptureTargetSize = remember { ImageTargetSize(AspectRatio.RATIO_4_3) }
    var captureMode by remember { mutableStateOf(CaptureMode.Image) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val contentResolver = context.contentResolver

    Scaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.BottomCenter
        ) {
            CameraPreview(
                modifier = Modifier,
                cameraState = cameraState,
                camSelector = camSelector,
                zoomRatio = zoomRatio,
                imageCaptureTargetSize = imageCaptureTargetSize,
                captureMode = captureMode
            ) {
                // Camera Preview UI
                Row {
                    Button(onClick = {
                        camSelector = if (camSelector == CamSelector.Back) {
                            CamSelector.Front
                        } else {
                            CamSelector.Back
                        }
                    }) {
                        Text(text = "切换摄像头")
                    }
                    Button(onClick = {

                        captureMode = CaptureMode.Image
                        val file = File(
                            Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES
                            ).absolutePath + File.separator + "${
                                System.currentTimeMillis()
                            }.png"
                        )

                        scope.launch {
                            cameraState.takePicture(file) {
                                when (it) {
                                    is ImageCaptureResult.Error -> {
                                        XLog.e("拍照失败")
                                        it.throwable.printStackTrace()
                                    }

                                    is ImageCaptureResult.Success -> {
                                        XLog.e("拍照成功==>${it.savedUri?.toFile()?.absolutePath}")
                                    }
                                }

                            }
                        }
                    }) {
                        Text(text = "拍照")
                    }

                    Button(onClick = {
                        captureMode = CaptureMode.Video
                        val file = File(
                            Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_MOVIES
                            ).absolutePath + File.separator + "${
                                System.currentTimeMillis()
                            }.mp4"
                        )

                        scope.launch {
                            if (cameraState.isRecording) {
                                cameraState.stopRecording()
                            } else {
                                cameraState.startRecording(
                                    FileOutputOptions.Builder(file).build()
                                ) {
                                    when (it) {
                                        is VideoCaptureResult.Error -> {
                                            XLog.e("录像失败")
                                            it.throwable?.printStackTrace()
                                        }

                                        is VideoCaptureResult.Success -> {
                                            XLog.e("录像成功==>${it.savedUri?.toFile()?.absolutePath}")
                                        }
                                    }
                                }
                            }
                        }


                    }) {

                        Text(text = if (cameraState.isRecording) "停止录像" else "开始录像")
                    }

                }


            }
        }
    }


}