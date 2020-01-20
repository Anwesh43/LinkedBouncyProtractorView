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

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()
