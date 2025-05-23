package com.github.sceneren.featurea.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.RectF
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Rational
import android.view.Surface
import androidx.annotation.RequiresPermission
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraEffect.IMAGE_CAPTURE
import androidx.camera.core.CameraEffect.PREVIEW
import androidx.camera.core.CameraEffect.VIDEO_CAPTURE
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OutputFileOptions
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.core.ViewPort
import androidx.camera.core.ViewPort.FILL_CENTER
import androidx.camera.effects.OverlayEffect
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat
import androidx.core.graphics.toRectF
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import java.io.File
import java.util.concurrent.Executors

class CameraXVM() : ViewModel(), ContainerHost<CameraXState, Nothing> {
    companion object {
        private const val TAG = "CameraController"
    }

    override val container = container<CameraXState, Nothing>(CameraXState()) {
        setCurrentTime()
    }

    private val textPaint = Paint().apply {
        this.color = Color.RED
        this.textAlign = Paint.Align.CENTER
    }

    private var cameraControl: CameraControl? = null

    private val handlerThread by lazy {
        HandlerThread("effect thread").apply {
            start()
        }
    }

    @SuppressLint("RestrictedApi")
    private val cameraPreviewUseCase = Preview.Builder().build().apply {

        setSurfaceProvider { newSurfaceRequest ->
            intent {
                reduce {
                    state.copy(surfaceRequest = newSurfaceRequest)
                }
            }
        }
    }

    private val recorder = Recorder.Builder()
        //.setQualitySelector(QualitySelector.getSupportedQualities())
        .build()
    private val videoCapture = VideoCapture.withOutput(recorder)

    private val imageCapture =
        ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

    private var processCameraProvider: ProcessCameraProvider? = null

    private val effectHandler = Handler(handlerThread.looper)

    private val overlayEffect = OverlayEffect(
        PREVIEW or VIDEO_CAPTURE or IMAGE_CAPTURE,
        0,
        effectHandler
    ) {}

    private val useCaseGroupBuilder = UseCaseGroup.Builder()
        .setViewPort(ViewPort.Builder(Rational(9, 16), Surface.ROTATION_0).apply {
            setScaleType(FILL_CENTER)
        }.build())
        .addUseCase(cameraPreviewUseCase)
        .addUseCase(videoCapture)
        .addUseCase(imageCapture)
        .addEffect(overlayEffect)

    @SuppressLint("RestrictedApi")
    suspend fun bindToCamera(appContext: Context, lifecycleOwner: LifecycleOwner) {
        if (processCameraProvider == null) {
            processCameraProvider = ProcessCameraProvider.awaitInstance(appContext)
        }
        processCameraProvider ?: return

        overlayEffect.clearOnDrawListener()

        overlayEffect.setOnDrawListener { frame ->

            frame.overlayCanvas.let { canvas ->
                canvas.save()
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                // 应用传感器到缓冲区的转换矩阵
                canvas.setMatrix(frame.sensorToBufferTransform)
                // 从中心点旋转canvas画布，使绘制方向变成水平
                canvas.rotate(-90f, canvas.width / 2f, canvas.height / 2f)
                // 获取可绘制区域
                val rect = canvas.clipBounds

                Log.e("rect", "$rect===${rect.width()},${rect.height()}")
                Log.e("canvas", "${canvas.width},  ${canvas.height}")
                val watermarkBitmap = container.stateFlow.value.waterMarkBitmap
                Log.e(
                    "bitmap",
                    "${watermarkBitmap?.width},  ${watermarkBitmap?.height}"
                )
                if (watermarkBitmap?.isRecycled == false) {
                    val watermarkRect = rect.toRectF()
                    canvas.drawBitmap(watermarkBitmap, null, watermarkRect, textPaint)
                }else{
                    Log.e("watermarkBitmap","watermarkBitmap==isRecycled")
                }
            }
            true
        }

        processCameraProvider?.bindToLifecycle(
            lifecycleOwner, DEFAULT_BACK_CAMERA, useCaseGroupBuilder.build()
        )
        cameraControl = cameraPreviewUseCase.camera?.cameraControl

        try {
            awaitCancellation()
        } finally {
            processCameraProvider?.unbindAll()
            cameraControl = null
        }
    }

    fun takePhoto() = intent {
        val path =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath
        if (!File("$path/TestCamera").exists()) {
            File("$path/TestCamera").mkdirs()
        }

        imageCapture.takePicture(
            OutputFileOptions.Builder(File("$path/TestCamera/test-${System.currentTimeMillis()}.jpg"))
                .build(),
            Executors.newSingleThreadExecutor(),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Log.e("CameraXVM", "onImageSaved")
                }

                override fun onError(exception: ImageCaptureException) {
                    exception.printStackTrace()
                }
            }
        )
    }

    private var recording: Recording? = null

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun switchRecord(context: Context) = intent {
        if (state.isRecording) {
            stopRecord()
        } else {
            startRecord(context)
        }
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    private fun startRecord(context: Context) = intent {
        val path =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).absolutePath
        if (!File("$path/TestCamera").exists()) {
            File("$path/TestCamera").mkdirs()
        }


        val outputFileOptions = FileOutputOptions.Builder(
            File("$path/TestCamera/test-${System.currentTimeMillis()}.mp4")
        ).build()

        recording = recorder.prepareRecording(context, outputFileOptions)
            .start(ContextCompat.getMainExecutor(context)) { videoRecordEvent ->
                when (videoRecordEvent) {
                    is VideoRecordEvent.Start -> {
                        // 开始录制
                        Log.e(TAG, "开始录制")
                        intent {
                            reduce {
                                state.copy(isRecording = true)
                            }
                        }
                    }

                    is VideoRecordEvent.Pause -> {
                        // 录制暂停
                        Log.e(TAG, "Pause")
                    }

                    is VideoRecordEvent.Resume -> {
                        // 录制恢复
                        Log.e(TAG, "Resume")
                    }

                    is VideoRecordEvent.Finalize -> {
                        // 录制完成
                        Log.e(TAG, "Finalize")
                        intent {
                            reduce {
                                state.copy(isRecording = false)
                            }
                        }

                    }
                }
            }


    }

    private fun stopRecord() {
        recording?.stop()
        recording = null
    }

    private fun setCurrentTime() = intent {
        flow {
            while (true) {
                emit(System.currentTimeMillis().timestampToFormattedDate())
                delay(500)
            }
        }.onEach {
            reduce {
                state.copy(currentTime = it)
            }
        }.launchIn(viewModelScope)
    }

    fun setWatermarkBitmap(bitmap: Bitmap?) = intent {
        //state.waterMarkBitmap?.recycle()
        reduce {
            state.copy(waterMarkBitmap = bitmap)
        }
    }

    fun setDeviceRotation(angle: Float) = intent {
        reduce {
            state.copy(deviceRotation = angle)
        }
    }

    public override fun onCleared() {
        intent {
            state.waterMarkBitmap?.recycle()
        }
        super.onCleared()
    }

}
