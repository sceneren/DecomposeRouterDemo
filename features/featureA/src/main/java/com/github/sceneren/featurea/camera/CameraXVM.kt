package com.github.sceneren.featurea.camera

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.camera.core.CameraEffect.IMAGE_CAPTURE
import androidx.camera.core.CameraEffect.PREVIEW
import androidx.camera.core.CameraEffect.VIDEO_CAPTURE
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
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

class CameraXVM : ViewModel(), ContainerHost<CameraXState, Nothing> {

    override val container = container<CameraXState, Nothing>(CameraXState())

    private val textPaint = Paint().apply {
        this.textSize = 60f
        this.color = Color.RED
        this.textAlign = Paint.Align.CENTER
    }

    private val cameraPreviewUseCase = Preview.Builder().build().apply {

        setSurfaceProvider { newSurfaceRequest ->
            intent {
                reduce {
                    state.copy(surfaceRequest = newSurfaceRequest)
                }
            }
        }
    }


    suspend fun bindToCamera(appContext: Context, lifecycleOwner: LifecycleOwner) {
        val processCameraProvider = ProcessCameraProvider.awaitInstance(appContext)

        val effectHandler = Handler(Looper.getMainLooper())

        val overlayEffect = OverlayEffect(
            PREVIEW or VIDEO_CAPTURE or IMAGE_CAPTURE,
            0,
            effectHandler
        ) {}

        overlayEffect.setOnDrawListener { frame ->
            frame.overlayCanvas.let { canvas ->
                Log.e("canvas", "canvas:${frame.rotationDegrees}")
                frame.overlayCanvas.drawColor(
                    Color.TRANSPARENT,
                    PorterDuff.Mode.CLEAR
                ) // Clear previous frame
                frame.overlayCanvas.setMatrix(frame.sensorToBufferTransform) // Apply transformation to align with the camera sensor

                val text = "Watermark ${System.currentTimeMillis()}"
                val centerX = frame.overlayCanvas.width / 2f
                val centerY = frame.overlayCanvas.height / 2f

                frame.overlayCanvas.drawText(
                    text,
                    centerX,
                    centerY,
                    textPaint
                ) // Draw text at the center


            }

            true
        }

        val recorder = Recorder.Builder()
            //.setQualitySelector(QualitySelector.getSupportedQualities())
            .build()

        val videoCapture = VideoCapture.withOutput(recorder)

        val useCaseGroupBuilder = UseCaseGroup.Builder()
            .addUseCase(cameraPreviewUseCase)
            .addUseCase(videoCapture)
            .addEffect(overlayEffect)

        processCameraProvider.bindToLifecycle(
            lifecycleOwner, DEFAULT_BACK_CAMERA, useCaseGroupBuilder.build()
        )

        // Cancellation signals we're done with the camera
        try {
            awaitCancellation()
        } finally {
            processCameraProvider.unbindAll()
        }
    }
}