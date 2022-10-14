package com.southgnss.drawcore.layers

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path

/**
 * Created by weipi on 2022/4/12.
 */
class MapComposeLayer : Layer {
    //region Scale

    private val scalePaint: Paint = Paint().apply {
        color = Color.GREEN
        strokeWidth = 1.0f
        style = Paint.Style.STROKE
        textSize = 30f
    }
    private val scalePath: Path = Path()
    private val scaleBasePoint = floatArrayOf(20f, 20f)
    private val scaleLenPx = 100f
    private val scaleHeiPx = 5f

    //endregion

    private val tagPointPaint = Paint().apply {
        color = Color.RED
        strokeWidth = 2.0f
        style = Paint.Style.FILL
        textSize = 30f
    }

    constructor() : super() {
        layerName="MapCompose"
        scalePath.moveTo(scaleBasePoint[0], scaleBasePoint[1])
        scalePath.lineTo(scaleBasePoint[0], scaleBasePoint[1] + scaleHeiPx)
        scalePath.lineTo(scaleBasePoint[0] + scaleLenPx, scaleBasePoint[1] + scaleHeiPx)
        scalePath.lineTo(scaleBasePoint[0] + scaleLenPx, scaleBasePoint[1])
    }


    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(scalePath, scalePaint)
        val str = String.format("%.3f", transformer.getGeoDistanceHor(scaleLenPx.toDouble()))
        val scaleMeasureLen = scalePaint.measureText(str)
        canvas.drawText(
            str,
            scaleBasePoint[0] + scaleLenPx / 2f - scaleMeasureLen / 2f,
            scaleBasePoint[1] + 2f,
            scalePaint
        )

        var geoSourcePointPx = transformer.geoToScreen(0.0, 0.0)
        canvas.drawCircle(
            geoSourcePointPx[0].toFloat(),
            geoSourcePointPx[1].toFloat(), 5f,
            tagPointPaint
        )
        geoSourcePointPx = transformer.getScreenCenter()
        canvas.drawCircle(
            geoSourcePointPx[0].toFloat(),
            geoSourcePointPx[1].toFloat(), 5f,
            tagPointPaint
        )
        val gCenter = transformer.getGeoCenter()
        canvas.drawText(
            "(${String.format("%.3f", gCenter[0])},${
                String.format("%.3f", gCenter[1])
            })",
            geoSourcePointPx[0].toFloat() + 5f,
            geoSourcePointPx[1].toFloat(), tagPointPaint
        )

        val s = "缩放比例${
            String.format(
                "1px:%.3fm",
                transformer.xScale
            )
        }"
        canvas.drawText(s, 20.0f, 50.0f, tagPointPaint)

        val start = transformer.geoToScreen(-1.75, 333.56149)
        val end = transformer.geoToScreen(0.0, 333.57904)
        canvas.drawLine(
            start[0].toFloat(),
            start[1].toFloat(),
            end[0].toFloat(),
            end[1].toFloat(),
            scalePaint
        )
    }
}