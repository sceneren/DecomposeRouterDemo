package com.github.sceneren.featurea.camera

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.camera.core.CameraEffect.IMAGE_CAPTURE
import androidx.camera.core.CameraEffect.PREVIEW
import androidx.camera.core.CameraEffect.VIDEO_CAPTURE
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.effects.OverlayEffect
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class CameraXVM : ViewModel(), ContainerHost<CameraXState, Nothing> {
    var surfaceRequest: SurfaceRequest? = null
    var overlayEffect: OverlayEffect? = null

    override val container = container<CameraXState, Nothing>(CameraXState())

    suspend fun bindToCamera(appContext: Context, lifecycleOwner: LifecycleOwner) {
        val processCameraProvider = ProcessCameraProvider.getInstance(appContext)
        val cameraProvider = withContext(Dispatchers.IO) {
            processCameraProvider.get()
        }
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider {}
        }
        val effectHandler = Handler(Looper.getMainLooper())

        overlayEffect =
            OverlayEffect(
                PREVIEW or VIDEO_CAPTURE or IMAGE_CAPTURE,
                0,
                effectHandler
            ) {}
        preview.setSurfaceProvider {

        }
//        preview.addEffect(effectDrawListener!!)
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        cameraProvider.unbindAll()
        try {
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
            )
        } catch (e: Exception) {
            Log.e("CameraPreview", "Error binding camera use cases", e)
        }

    }

    fun getEffectDrawListener() = overlayEffect
}