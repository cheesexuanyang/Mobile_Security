package com.example.inf2007_mad_j1847.view.attack

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity

class MaskingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the content to our custom view instead of a Compose layout
        setContentView(MaskingView(this))
    }

    /**
     * A custom View that draws a white background and punches a transparent hole in it.
     */
    private class MaskingView(context: Context) : View(context) {

        private val whitePaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }

        private val clearPaint = Paint().apply {
            // This is the magic part.
            // SRC_OUT mode means "draw only where the source is outside of the destination".
            // Since we are drawing a new rectangle (the source) on top of the existing
            // canvas (the destination), this effectively clears the pixels in that rectangle.
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
            color = Color.TRANSPARENT // The color doesn't matter much, but transparent is clearest
        }

        // The coordinates and size of our transparent "hole"
        // We'll start with a 1x1 pixel hole at coordinate (100, 100)
        private val holeRect = Rect(100, 100, 101, 101)

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            // Step 1: Draw the solid white background across the entire canvas.
            // We need to use a new layer for the PorterDuff mode to work correctly.
            canvas.saveLayer(null, null) // Save the canvas state
            canvas.drawPaint(whitePaint)

            // Step 2: "Punch out" the transparent hole.
            // The clearPaint will erase the pixels within the bounds of holeRect.
            canvas.drawRect(holeRect, clearPaint)

            canvas.restore() // Restore the canvas to its previous state
        }
    }
}