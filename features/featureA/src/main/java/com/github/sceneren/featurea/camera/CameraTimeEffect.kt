package com.github.sceneren.featurea.camera

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.camera.core.Preview
import androidx.camera.effects.OverlayEffect
import androidx.core.util.Consumer

@SuppressLint("RestrictedApi")
class CameraTimeEffect(
    targets: Int,
    private val textSize: Float,
    private val preview: Preview
) : OverlayEffect(
    targets,
    0,
    Handler(Looper.getMainLooper()),
    Consumer { t -> Log.d("CameraTimeEffect", "Effect error", t) }
) {
    private val textPaint =
        Paint().apply {
            color = Color.YELLOW
            textSize = this@CameraTimeEffect.textSize
        }

    init {
        setOnDrawListener { frame ->
            Log.e("frame","frame==>${frame.rotationDegrees}")
            val sensorToUi = preview.sensorToBufferTransformMatrix
            val rect = preview.viewPortCropRect ?: return@setOnDrawListener true
            if (sensorToUi != null) {
                // Transform the Canvas to use PreviewView coordinates.
                val sensorToEffect = frame.sensorToBufferTransform
                val uiToSensor = Matrix()
                sensorToUi.invert(uiToSensor)
                uiToSensor.postConcat(sensorToEffect)
                val canvas = frame.overlayCanvas
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                canvas.setMatrix(uiToSensor)

                // measure the size of the text
                val bounds = Rect()
                val logoText = "Watermark ${System.currentTimeMillis()}"
                textPaint.getTextBounds(logoText, 0, logoText.length, bounds)

                // Draw an oval and the text within.
                //rect.width().toFloat() - bounds.width().toFloat() / 2,
                //                    rect.height().toFloat() + bounds.height().toFloat() / 2,
                canvas.drawText(
                    logoText,
                    100f,800f,
                    textPaint
                )
            }
            true
        }
    }


}