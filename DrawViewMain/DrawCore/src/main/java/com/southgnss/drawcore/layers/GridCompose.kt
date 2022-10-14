package com.southgnss.drawcore.layers

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import com.southgnss.drawcore.IDrawable
import com.southgnss.drawcore.ViewTransformer

/**
 * Created by weipi on 2022/5/18.
 * From project DrawViewMain
 */
class GridCompose(private val transformer: ViewTransformer) : IDrawable {
    var show = false
    var split = 50.0

    private val horGridLinePaint: Paint by lazy {
        Paint().apply {
            color = Color.GRAY
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }
    }

    private val textPaint: Paint by lazy {
        Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 1f
            textSize=20f
        }
    }

    override fun onDraw(canvas: Canvas) {
        //计算左下角坐标
        val leftBottomGeo = transformer.screenToGeo(0.0, canvas.height.toDouble())
        var verSplitPx = transformer.getScreenDistanceVer(split)

        var next = canvas.height.toDouble()

        while (true) {
            next -= verSplitPx
            if (next < 0) {
                break
            }
            val nextF = next.toFloat()
            canvas.drawLine(0f, nextF, canvas.width.toFloat(), nextF, horGridLinePaint)
            val geoY = transformer.screenToGeo(0.0, next)[1]
            canvas.drawText(String.format("%1.3f",geoY),5f,nextF,textPaint)
        }


        Log.i("debug", "${leftBottomGeo[0]}:${leftBottomGeo[1]}")
    }
}