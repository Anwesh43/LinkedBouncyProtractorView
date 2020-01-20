package com.anwesh.uiprojects.bouncyprotractorview

/**
 * Created by anweshmishra on 20/01/20.
 */

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import android.app.Activity
import android.content.Context

val nodes : Int = 5
val lines : Int = 2
val scGap : Float = 0.02f
val sizeFactor : Float = 2.9f
val backColor : Int = Color.parseColor("#BDBDBD")
val foreColor : Int = Color.parseColor("#311B92")
val strokeFactor : Int = 90
val delay : Long = 20
val rFactor : Float = 3.2f
val deg : Float = -60f
val sweepDeg : Float = 360f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawProtractorLine(j : Int, sf : Float, size : Float, paint : Paint) {
    save()
    rotate(deg * (1f - 2 * j) * sf.divideScale(j, lines))
    drawLine(0f, 0f, 0f, size, paint)
    restore()
}

fun Canvas.drawBouncyProtractor(scale : Float, size : Float, paint : Paint) {
    val r : Float = size / rFactor
    val sf : Float = scale.sinify()
    for (j in 0..(lines - 1)) {
        drawProtractorLine(j, sf, size, paint)
    }
    drawArc(RectF(-r, -r, r, r), 0f, sweepDeg * sf, true, paint)
}

fun Canvas.drawBPNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = h / (nodes + 1)
    val size : Float = gap / sizeFactor
    paint.color = foreColor
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    save()
    translate(w / 2, gap * (i + 1))
    drawBouncyProtractor(scale, size, paint)
    restore()
}

class BouncyProtractorView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}