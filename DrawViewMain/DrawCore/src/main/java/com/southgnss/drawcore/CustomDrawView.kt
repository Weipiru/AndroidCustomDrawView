package com.southgnss.drawcore

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.southgnss.drawcore.layers.GridCompose
import com.southgnss.drawcore.layers.Layer
import com.southgnss.drawcore.layers.MapComposeLayer


/**
 * Created by weipi on 2022/4/11.
 */
class CustomDrawView : View, View.OnTouchListener {
    constructor(context: Context?) : super(context) {
        initSelf()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initSelf()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initSelf()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initSelf()
    }


    private var transformer: ViewTransformer? = null
    private var touchController: TouchController = TouchController().apply {
        touchListener = object : TouchController.TouchMapListener {
            override fun onDrag(xDiff: Double, yDiff: Double) {
                this@CustomDrawView.transformer!!.translate(xDiff, -yDiff)
                invalidate()
            }

            override fun onZoomIn(centerX: Double, centerY: Double, zoomDist: Double) {
                this@CustomDrawView.transformer!!.zoomIn(centerX, centerY)
                invalidate()
            }

            override fun onZoomOut(centerX: Double, centerY: Double, zoomDist: Double) {
                this@CustomDrawView.transformer!!.zoomOut(centerX, centerY)
                invalidate()
            }

        }
    }

    private val rect = Rect();

    private val paint = Paint().apply {
        color = Color.RED
        strokeWidth = 10f
        style = Paint.Style.FILL
        textSize = 20f
    }

    private val layers = ArrayList<Layer>()
    lateinit var gridCompose: GridCompose
        private set
    lateinit var mapComposeLayer: MapComposeLayer

    //region Proper


    //endregion

    private fun initSelf() {
        this.transformer = ViewTransformer()
        //        addLayer(MapComposeLayer())
        setOnTouchListener(this)
        initCompose()
    }

    private fun initCompose() {
        gridCompose = GridCompose(this.transformer!!)
        mapComposeLayer = MapComposeLayer()
        mapComposeLayer.register(transformer!!, this)
    }

    override fun onDraw(canvas: Canvas) {
        if (!canvas.getClipBounds(rect)) {
            return
        }
        if (transformer == null) return

        transformer!!.restViewRect(rect)

        for (layer in layers) {
            layer.onDraw(canvas)
        }

        canvas.drawCircle(
            touchController.translateStartPoint[0].toFloat(),
            touchController.translateStartPoint[1].toFloat(),
            15f,
            paint.apply { color = Color.CYAN }
        )
        canvas.drawCircle(
            touchController.zoomCenterPoint[0].toFloat(),
            touchController.zoomCenterPoint[1].toFloat(),
            5f,
            paint.apply { color = Color.BLACK }
        )

        if (gridCompose.show) {
            gridCompose.onDraw(canvas)
        }
        mapComposeLayer.onDraw(canvas)
    }

    private var screenPointArr: FloatArray? = null
    fun setData(): Unit {
    }


    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return touchController.onTouch(event)
    }


    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(event)
    }

    fun zoomAll() {
        val geoToScreen = transformer!!.geoToScreen(-1.75, 33.56149)
        transformer?.moveGeoPointToCenter(doubleArrayOf(2569169.885710221, 132980.60698954226))
        invalidate()
    }

    fun addLayer(layer: Layer): Boolean {
        val tempLayer = layers.singleOrNull { it.layerName == layer.layerName }
        if (tempLayer != null) return false
        layer.register(this.transformer!!, this)
        layers.add(layer)
        return true
    }

    fun requestRefresh() {
        invalidate()
    }
}