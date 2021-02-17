package com.shaiicodez.minipaint

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Instance of MyCanvasView
        val myCanvasView = MyCanvasView(this)
        // Request the full screen for the layout of myCanvasView
        myCanvasView.systemUiVisibility = SYSTEM_UI_FLAG_FULLSCREEN
        // Add a content description
        myCanvasView.contentDescription = getString(R.string.canvasContentDescription)
        // Set the content view to myCanvasView
        setContentView(myCanvasView)
    }
}