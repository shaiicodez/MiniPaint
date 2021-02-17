package com.shaiicodez.minipaint

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat


// A constant for the stroke width
private const val STROKE_WIDTH = 12f // has to be float

class MyCanvasView(context: Context) : View(context) {

    // These are your bitmap and canvas for caching what has been drawn before.
    //private lateinit var extraCanvas: Canvas
    //private lateinit var extraBitmap: Bitmap
    // Path representing the drawing so far
    private val drawing = Path()

    // Path representing what's currently being drawn
    private val curPath = Path()

    // A class level variable backgroundColor, for the background color of the canvas
    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)

    // For holding the color to draw with
    private val drawColor = ResourcesCompat.getColor(resources, R.color.colorPaint, null)

    // variable path and initialize it with a Path object
    // to store the path that is being drawn when following the user's touch on the screen.
    private var path = Path()

    // variables to cache the latest x and y values
    private var currentX = 0f
    private var currentY = 0f

    //variables for caching the x and y coordinates of the current touch event
    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f

    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    private lateinit var frame: Rect

    // Set up the paint with which to draw.
    private val paint = Paint().apply {
        color = drawColor
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        isDither = true
        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)
    }

    /**
     * This callback method is called by the Android system with the changed screen dimensions,
     * that is, with a new width and height (to change to)
     * and the old width and height (to change from).
     **/
    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        /**
         *  a new bitmap and canvas are created every time the function executes.
         *  You need a new bitmap, because the size has changed.
         *  However, this is a memory leak, leaving the old bitmaps around.
         *  To fix this, recycle extraBitmap before creating the next one.
         **/

        //if (::extraBitmap.isInitialized) extraBitmap.recycle()
        // An instance of Bitmap with the new width and height
        //extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        //extraCanvas = Canvas(extraBitmap)
        //extraCanvas.drawColor(backgroundColor)

        // Calculate a rectangular frame around the picture.
        val inset = 40
        frame = Rect(inset, inset, width - inset, height - inset)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //canvas.drawBitmap(extraBitmap, 0f, 0f, null)

        // Draw the drawing so far
        canvas.drawPath(drawing, paint)
        // Draw any current squiggle
        canvas.drawPath(curPath, paint)
        // Draw a frame around the canvas
        canvas.drawRect(frame, paint)
    }

    //touch event methods

    //  Reset the path,
    //  move to the x-y coordinates of the touch event (motionTouchEventX and motionTouchEventY)
    //  and assign currentX and currentY to that value
    private fun touchStart() {
        path.reset()
        path.moveTo(motionTouchEventX, motionTouchEventY)
        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }

    // Calculate the traveled distance (dx, dy), create a curve between the two points and store it in path,
    // update the running currentX and currentY tally, and draw the path.
    // Then call invalidate() to force redrawing of the screen with the updated path
    private fun touchMove() {
        val dx = Math.abs(motionTouchEventX - currentX)
        val dy = Math.abs(motionTouchEventY - currentY)
        if (dx >= touchTolerance || dy >= touchTolerance) {
            // QuadTo() adds a quadratic bezier from the last point,
            // approaching control point (x1,y1), and ending at (x2,y2).
            path.quadTo(currentX, currentY, (motionTouchEventX + currentX) / 2, (motionTouchEventY + currentY) / 2)
            currentX = motionTouchEventX
            currentY = motionTouchEventY
            // Draw the path in the extra bitmap to cache it.
            //extraCanvas.drawPath(path, paint)
        }
        invalidate()
    }

    private fun touchUp() {
        // Reset the path so it doesn't get drawn again.
        //path.reset()
        // Add the current path to the drawing so far
        drawing.addPath(curPath)
        // Rewind the current path for the next touch
        curPath.reset()
    }

    // method to respond to motion on the display
    //  cache the x and y coordinates of the passed in event
    override fun onTouchEvent(event: MotionEvent): Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchUp()
        }
        return true
    }

}//end class view canvas