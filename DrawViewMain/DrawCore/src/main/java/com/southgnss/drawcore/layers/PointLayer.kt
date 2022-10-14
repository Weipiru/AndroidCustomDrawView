package com.southgnss.drawcore.layers

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.southgnss.drawcore.Coordinate
import com.southgnss.drawcore.CustomDrawView
import com.southgnss.drawcore.ViewTransformer

/**
 * Created by weipi on 2022/4/13.
 */
class PointLayer() : Layer() {
    private val points = ArrayList<Coordinate>()

    private val pointPaint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.FILL
    }

    private val linePaint = Paint().apply {
        color = Color.GRAY
        style = Paint.Style.FILL
        textSize = 20f
    }

    private val pointSize = 5f
    private val pointCellSize = 10 //格网宽度 px
    private var pointStatusContainer: Array<BooleanArray>? = null
    private val debug = false

    //自动把重叠显示点剔除
    val enableOptimization = true
    var drawPointCounter = 0
        private set

    override fun register(transformer: ViewTransformer, mapView: CustomDrawView) {
        super.register(transformer, mapView)
    }

    override fun onDraw(canvas: Canvas) {
        drawPointCounter = 0
        val gridW: Int = (canvas.width / pointCellSize) + 1
        val gridH: Int = (canvas.height / pointCellSize) + 1
        pointStatusContainer = Array(gridW) { BooleanArray(gridH) { false } }
        if (debug) {
            var xTemp = 0f
            for (i in 0..gridW) {
                canvas.drawLine(xTemp, 0f, xTemp, canvas.height.toFloat(), linePaint)
                xTemp += pointCellSize
            }

            var yTemp = 0f
            for (i in 0..gridH) {
                canvas.drawLine(0f, yTemp, canvas.width.toFloat(), yTemp, linePaint)
                yTemp += pointCellSize
            }
        }


        for (point in points) {
            val px = this.transformer.geoToScreen(point.x, point.y)
            if (px[0] < 0 || px[0] > canvas.width || px[1] < 0 || px[1] > canvas.height) continue

            if (enableOptimization) {
                val gridX = (px[0] / pointCellSize).toInt()
                val gridY = (px[1] / pointCellSize).toInt()
                val status = pointStatusContainer!![gridX][gridY]
                if (status) {
                    continue
                }
                pointStatusContainer!![gridX][gridY] = true
            }
            canvas.drawCircle(px[0].toFloat(), px[1].toFloat(), pointSize, pointPaint)
            drawPointCounter++
        }

        canvas.drawText("$layerName:$drawPointCounter", 50f, 150f, linePaint)
    }

    fun add(coordinate: Coordinate) {
        this.points.add(coordinate)
    }
}