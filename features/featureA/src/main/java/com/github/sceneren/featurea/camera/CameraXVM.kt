package com.github.sceneren.featurea.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Rational
import android.view.Surface
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
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.awaitCancellation
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import java.io.File
import java.util.concurrent.Executors

class CameraXVM() : ViewModel(), ContainerHost<CameraXState, Nothing> {

    override val container = container<CameraXState, Nothing>(CameraXState())

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

    fun setTextSize(textSize: Float) {
        textPaint.textSize = textSize
    }

    @SuppressLint("RestrictedApi")
    private val cameraPreviewUseCase = Preview.Builder().build().apply {

        setSurfaceProvider { newSurfaceRequest ->
            intent {
                reduce {
                    newSurfaceRequest.camera.cameraControlInternal.sensorRect
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

    @SuppressLint("RestrictedApi")
    suspend fun bindToCamera(appContext: Context, lifecycleOwner: LifecycleOwner) {
        val processCameraProvider = ProcessCameraProvider.awaitInstance(appContext)

        val effectHandler = Handler(handlerThread.looper)

        val overlayEffect = OverlayEffect(
            PREVIEW or VIDEO_CAPTURE or IMAGE_CAPTURE,
            0,
            effectHandler
        ) {}

        val cameraTimeEffect = CameraTimeEffect(
            targets = PREVIEW or VIDEO_CAPTURE or IMAGE_CAPTURE,
            textSize = textPaint.textSize,
            preview = cameraPreviewUseCase
        )

        overlayEffect.clearOnDrawListener()
        overlayEffect.setOnDrawListener { frame ->
            cameraPreviewUseCase.camera?.cameraControlInternal?.sensorRect
            val previewRect = cameraPreviewUseCase.viewPortCropRect


            frame.overlayCanvas.let { canvas ->
                canvas.setMatrix(frame.sensorToBufferTransform)
                canvas.drawColor(
                    Color.TRANSPARENT,
                    PorterDuff.Mode.CLEAR
                )
                Log.e("textSize3", "${canvas.width},${canvas.height}")
                val matrix = Matrix()
                matrix.postConcat(frame.sensorToBufferTransform)
                matrix.postRotate(
                    -frame.rotationDegrees.toFloat(),
                    (canvas.width / 2f),
                    (canvas.height / 2f)
                )
                previewRect?.let { rect ->
                    Log.e("textSize3", "${rect}, ${canvas.width},${canvas.height}")
                    canvas.setMatrix(matrix)
                    val text = "Watermark ${System.currentTimeMillis()}"
                    val centerX = canvas.width / 2f
                    val centerY = canvas.height / 2f
                    canvas.drawText(
                        text,
                        centerX,
                        centerY,
                        textPaint
                    )
                }

            }
            true
        }

        val useCaseGroupBuilder = UseCaseGroup.Builder()
            .setViewPort(ViewPort.Builder(Rational(9, 16), Surface.ROTATION_0).apply {
                setScaleType(FILL_CENTER)
            }.build())
            .addUseCase(cameraPreviewUseCase)
            .addUseCase(videoCapture)
            .addUseCase(imageCapture)
            .addEffect(cameraTimeEffect)

        processCameraProvider.bindToLifecycle(
            lifecycleOwner, DEFAULT_BACK_CAMERA, useCaseGroupBuilder.build()
        )
        cameraControl = cameraPreviewUseCase.camera?.cameraControl

        try {
            awaitCancellation()
        } finally {
            processCameraProvider.unbindAll()
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

}

fun applyScaleCrop(
    matrix: Matrix,
    imageWidth: Float,
    imageHeight: Float,
    targetWidth: Float,
    targetHeight: Float
) {
    // 计算缩放比例
    val scaleX = targetWidth / imageWidth
    val scaleY = targetHeight / imageHeight
    val scale = scaleX.coerceAtLeast(scaleY) // 使用较大的比例

    // 创建缩放矩阵
    matrix.postScale(scale, scale)

    // 计算缩放后的图像尺寸
    val scaledWidth = imageWidth * scale
    val scaledHeight = imageHeight * scale

    // 计算平移量，使图像居中
    val dx = (targetWidth - scaledWidth) / 2f
    val dy = (targetHeight - scaledHeight) / 2f

    // 创建平移矩阵
    matrix.postTranslate(dx, dy)
}
