package com.example.inf2007_mad_j1847.view.attack

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi

@RequiresApi(31) // This API requires Android 12 (S) or higher
class EnlargementActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This is the key part of the enlargement layer.
        // It tells the window manager to blur the content behind this window.
        window.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)

        // We can fine-tune the blur effect.
        // A large radius will make it a solid color, effectively "enlarging" the pixel.
        window.attributes.blurBehindRadius = 200

        // Note: For a true "pixel stretch", you would also crop the blur source
        // to only the 1x1 pixel area. For now, this demonstrates the blur effect.
    }
}