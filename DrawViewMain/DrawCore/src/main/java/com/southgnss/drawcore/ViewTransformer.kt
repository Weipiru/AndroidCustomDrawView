package com.southgnss.drawcore

import android.graphics.Rect

const val TAG = "Transformer"

class ViewTransformer {

    //region 绘图参数定义
    /**
     * 绘图范围
     */
    private var viewRect: Rect = Rect();

    //    private var viewRect.width() = 100.0
    //    private var viewRect.height() = 100.0

    /**
     * X 方向缩放比例
     */
    var xScale = 1.0;

    /**
     * Y 方向缩放比例
     */
    private val yScale: Double
        get() {
            return xScale
        }

    /**
     * X 缩放范围
     */
    private var xScaleRange = doubleArrayOf(0.001, Double.MAX_VALUE)

    /**
     * Y 缩放范围
     */
    private var yScaleRange = doubleArrayOf(0.001, Double.MAX_VALUE)

    /**
     * 缩放间隔
     */
    var zoomSplit = 0.001

    /**
     * 改变不同比例尺下缩放速度
     */
    val zoomLevel: Double
        get() {
            var level = xScale * 20
            if (level == 0.0) level = 0.1
            if (level >= 30) level = 30.0
            println("Level: $level")
            return level
        }

    /**
     * 左上角实地坐标
     */
    var baseGeoPoint = doubleArrayOf(0.0, 0.0)

    //endregion

    //region  公有方法

    fun restViewRect(rectangle: Rect) {
        this.viewRect = rectangle
    }

    //region Zoom

    fun zoomIn() {
        xScaleAdd()
        yScaleAdd()
    }

    /**
     * 以指定点放大
     */
    fun zoomIn(screenPointX: Double, screenPointY: Double) {
        var startGeoPoint: DoubleArray? = null
        startGeoPoint = screenToGeo(screenPointX, screenPointY)
        zoomIn()
        val screenAfterScale = geoToScreen(startGeoPoint[0], startGeoPoint[1])
        moveScreenPointTo(screenPointX, screenPointY, screenAfterScale[0], screenAfterScale[1])
    }

    fun zoomOut() {
        xScaleSub()
        yScaleSub()
    }

    fun zoomOut(screenPointX: Double, screenPointY: Double) {
        var startGeoPoint: DoubleArray? = null
        startGeoPoint = screenToGeo(screenPointX, screenPointY)

        zoomOut()

        val screenAfterScale = geoToScreen(startGeoPoint[0], startGeoPoint[1])
        moveScreenPointTo(screenPointX, screenPointY, screenAfterScale[0], screenAfterScale[1])
    }

    fun zoomToRect(geoX: Double, geoY: Double, rectangle: Rect) {
        if (rectangle.width() <= 30 || rectangle.height() <= 30) {
            return
        }

        val gx = geoX + rectangle.width() / 2.0
        val gy = geoY + rectangle.height() / 2.0
        val geo = screenToGeo(gx, gy)
        xScale /= (viewRect.width() / rectangle.width())
        //        yScale = xScale
        moveGeoPointToCenter(geo)
    }

    fun restZoom() {
        xScale = 1.0
        //        yScale = 1.0
    }

    //endregion

    /**
     * 平移
     */
    fun translate(x: Double, y: Double) {
        //        val nX = if (abs(x) <= 2.0) 0.0 else x
        //        val nY = if (abs(y) <= 2.0) 0.0 else y
        //        Log.i(TAG, "translate: $x:$y")
        val nX = x
        val nY = y
        baseGeoPoint[0] -= getGeoDistanceHor(nX)
        baseGeoPoint[1] -= getGeoDistanceVer(nY)
        //        Log.i("TAG", "translate: ${nX}:$nY")
    }

    /**
     * 移动屏幕点到指定屏幕点位置
     */
    fun moveScreenPointTo(
        screenStartPointX: Double,
        screenStartPointY: Double,
        screenTargetPointX: Double,
        screenTargetPointY: Double
    ) {
        val xDiff = screenTargetPointX - screenStartPointX
        val yDiff = screenTargetPointY - screenStartPointY
        baseGeoPoint[0] += xDiff * xScale
        baseGeoPoint[1] -= yDiff * yScale
    }

    /**
     * 居中指定地理坐标
     */
    fun moveGeoPointToCenter(geoPoint: DoubleArray) {
        val centerGeo = getGeoCenter()
        val xDiff = centerGeo[0] - geoPoint[0]
        val yDiff = centerGeo[1] - geoPoint[1]
        baseGeoPoint[0] -= xDiff
        baseGeoPoint[1] -= yDiff
    }

    /**
     * 屏幕坐标转地理坐标
     */
    fun screenToGeo(screenPointX: Double, screenPointY: Double): DoubleArray {
        val geoY = viewRect.height() - screenPointY
        val geoX = screenPointX
        val rXY = doubleArrayOf(0.0, 0.0)
        rXY[0] = baseGeoPoint[0] + geoX * xScale;
        rXY[1] = baseGeoPoint[1] + geoY * yScale
        return rXY
    }

    /**
     * 地理坐标转屏幕坐标
     */
    fun geoToScreen(geoPointX: Double, geoPointY: Double): DoubleArray {
        val sX = (geoPointX - baseGeoPoint[0]) / xScale
        var sY = (geoPointY - baseGeoPoint[1]) / yScale
        sY = this.viewRect.height() - sY;
        return doubleArrayOf(sX, sY)
    }

    /**
     * 获取视图中心地理坐标
     */
    fun getGeoCenter(): DoubleArray {
        val screenCenter = getScreenCenter()
        return screenToGeo(screenCenter[0], screenCenter[1])
    }

    /**
     * 获取视图中心屏幕坐标
     */
    fun getScreenCenter(): DoubleArray {
        val sPoint = DoubleArray(2)

        sPoint[0] = viewRect.centerX().toDouble()
        sPoint[1] = viewRect.centerY().toDouble()
        return sPoint
    }

    /**
     * 屏幕距离转实地距离
     */
    fun getGeoDistanceHor(screenDistance: Double): Double {
        return screenDistance * xScale
    }

    /**
     * 实地距离转屏幕距离
     */
    fun getScreenDistanceHor(geoDist: Double): Double {
        return geoDist / xScale
    }

    /**
     * 屏幕距离转实地距离
     */
    fun getGeoDistanceVer(screenDistance: Double): Double {
        return screenDistance * yScale
    }

    /**
     * 实地距离转屏幕距离
     */
    fun getScreenDistanceVer(geoDist: Double): Double {
        return geoDist / yScale
    }

    /**
     * 获取边界实地坐标
     * return minX, maxX, minY, maxY
     */
    fun getViewGeoBound(): DoubleArray {
        val minX = baseGeoPoint[0]
        val maxX = minX + getGeoDistanceHor(viewRect.width().toDouble())
        val minY = baseGeoPoint[1]
        val maxY = baseGeoPoint[1] + getGeoDistanceVer(viewRect.height().toDouble())
        return doubleArrayOf(minX, maxX, minY, maxY)
    }

    //endregion

    //region 私有方法

    private fun xScaleAdd() {
        this.xScale += (zoomSplit * zoomLevel)
        if (this.xScale > xScaleRange[1]) {
            this.xScale = xScaleRange[1]
        }

    }

    private fun xScaleSub() {
        this.xScale -= (zoomSplit * zoomLevel)
        if (this.xScale <= xScaleRange[0]) {
            this.xScale = xScaleRange[0]
        }
    }

    private fun yScaleAdd() {
        //        this.yScale += (zoomSplit * zoomLevel)
        //        if (this.yScale > yScaleRange[1]) {
        //            this.yScale = yScaleRange[1]
        //        }
    }

    private fun yScaleSub() {
        //        this.yScale -= (zoomSplit * zoomLevel)
        //        if (this.yScale <= yScaleRange[0]) {
        //            this.yScale = yScaleRange[0]
        //        }
    }

    //endregion
}