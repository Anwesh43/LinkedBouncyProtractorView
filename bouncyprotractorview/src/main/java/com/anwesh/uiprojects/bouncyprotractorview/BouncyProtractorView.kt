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
val scGap : Float = 0.02f / lines
val sizeFactor : Float = 2.9f
val backColor : Int = Color.parseColor("#BDBDBD")
val foreColor : Int = Color.parseColor("#311B92")
val strokeFactor : Int = 90
val delay : Long = 15
val rFactor : Float = 3.2f
val deg : Float = -30f
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
    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()

            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class BPNode(var i : Int, val state : State = State()) {

        private var next : BPNode? = null
        private var prev : BPNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = BPNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawBPNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : BPNode {
            var curr : BPNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class BouncyProtractor(var i : Int) {

        private val root : BPNode = BPNode(0)
        private var curr : BPNode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : BouncyProtractorView) {

        private val animator : Animator = Animator(view)
        private val bp : BouncyProtractor = BouncyProtractor(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            bp.draw(canvas, paint)
            animator.animate {
                bp.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            bp.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : BouncyProtractorView {
            val view : BouncyProtractorView = BouncyProtractorView(activity)
            activity.setContentView(view)
            return view
        }
    }
}