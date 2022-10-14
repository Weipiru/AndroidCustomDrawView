package com.southgnss.drawcore

import android.view.MotionEvent
import android.view.MotionEvent.INVALID_POINTER_ID
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Created by weipi on 2022/9/28.
 * From project DrawViewMain
 */
class TouchController {
    private val MIN_THRESHOLD = 1

    var touchListener: TouchMapListener? = null

    val zoomCenterPoint = doubleArrayOf(0.0, 0.0)
    val translateStartPoint = doubleArrayOf(0.0, 0.0)

    var lastDistance = 0.0
    private var mActivePointerId = INVALID_POINTER_ID

    fun onTouch(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                event.actionIndex.also { pointerIndex ->
                    translateStartPoint[0] = event.getX(pointerIndex).toDouble()
                    translateStartPoint[1] = event.getY(pointerIndex).toDouble()
                }

                mActivePointerId = event.getPointerId(0)
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                calculateCenter(event).also {
                    zoomCenterPoint[0] = it[0]
                    zoomCenterPoint[1] = it[1]
                    translateStartPoint[0] = it[0]
                    translateStartPoint[1] = it[1]
                }

                lastDistance = spacing(event)

            }
            MotionEvent.ACTION_POINTER_UP -> {
                event.actionIndex.also { pointerIndex ->
                    val pointerId = event.getPointerId(pointerIndex)
                    if (pointerId == mActivePointerId) {
                        val newPointerIndex = if (pointerIndex == 0) 1 else 0
                        translateStartPoint[0] = event.getX(newPointerIndex).toDouble()
                        translateStartPoint[1] = event.getY(newPointerIndex).toDouble()
                        mActivePointerId = event.getPointerId(newPointerIndex)
                    } else {
                        translateStartPoint[0] =
                            event.getX(event.findPointerIndex(mActivePointerId)).toDouble()
                        translateStartPoint[1] =
                            event.getY(event.findPointerIndex(mActivePointerId)).toDouble()
                    }
                }
            }

            MotionEvent.ACTION_MOVE -> {
                val translateEndPoint = doubleArrayOf(0.0, 0.0)
                event.findPointerIndex(mActivePointerId).let { pointerIndex ->
                    translateEndPoint[0] = event.getX(pointerIndex).toDouble()
                    translateEndPoint[1] = event.getY(pointerIndex).toDouble()
                }

                if (event.pointerCount >= 2) {
                    calculateCenter(event).also {
                        zoomCenterPoint[0] = it[0]
                        zoomCenterPoint[1] = it[1]

                        translateEndPoint[0] = it[0]
                        translateEndPoint[1] = it[1]
                    }

                    val currentDistance = spacing(event)
                    val distanceDiff = currentDistance - lastDistance
                    if (abs(distanceDiff) > MIN_THRESHOLD) {
                        if (distanceDiff < 0) {
                            touchListener?.onZoomIn(
                                zoomCenterPoint[0],
                                zoomCenterPoint[1],
                                distanceDiff
                            )
                        } else {
                            touchListener?.onZoomOut(
                                zoomCenterPoint[0],
                                zoomCenterPoint[1],
                                distanceDiff
                            )
                        }
                        lastDistance = currentDistance
                    }
                }

                val xDiff = translateEndPoint[0] - translateStartPoint[0]
                val yDiff = translateEndPoint[1] - translateStartPoint[1]

                if (abs(xDiff) > MIN_THRESHOLD || abs(yDiff) > MIN_THRESHOLD) {
                    touchListener?.onDrag(xDiff, yDiff)
                }

                translateStartPoint[0] = translateEndPoint[0]
                translateStartPoint[1] = translateEndPoint[1]
            }
            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP -> {
                mActivePointerId = INVALID_POINTER_ID
            }
        }
        return true
    }

    /**
     * 计算连个手指连线中心点
     */
    private fun calculateCenter(event: MotionEvent): DoubleArray {
        //计算起点中心坐标
        val x0 = event.getX(0)
        val y0 = event.getY(0)

        val x1 = event.getX(1)
        val y1 = event.getY(1)

        return doubleArrayOf(
            x0 + (x1 - x0) / 2.0,
            y0 + (y1 - y0) / 2.0
        )
    }


    /**
     * 计算两个点的距离
     *
     * @param event
     * @return
     */
    private fun spacing(event: MotionEvent): Double {
        return if (event.pointerCount == 2) {
            val x = event.getX(0) - event.getX(1)
            val y = event.getY(0) - event.getY(1)
            sqrt((x * x + y * y).toDouble())
        } else 0.0
    }

    interface TouchMapListener {
        fun onDrag(xDiff: Double, yDiff: Double)
        fun onZoomIn(centerX: Double, centerY: Double, zoomDist: Double)
        fun onZoomOut(centerX: Double, centerY: Double, zoomDist: Double)
    }
}