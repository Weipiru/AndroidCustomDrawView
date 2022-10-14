package com.southgnss.drawviewmain

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.southgnss.drawcore.layers.Layer

/**
 * Created by weipi on 2022/9/30.
 * From project DrawViewMain
 */
class LocationLayer : Layer() {
    init {
        layerName = "LocationLayer"
    }

    private lateinit var locationManager: LocationManager
    private var mLocation: Location? = null

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            mLocation = location
            mapView.requestRefresh()
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

        }

        override fun onProviderEnabled(provider: String) {

        }

        override fun onProviderDisabled(provider: String) {

        }
    }

    fun startLocationUpdates(mContext: Context) {
        locationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                mContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0L,
            0f,
            locationListener
        )
    }

    fun stopLocationUpdates() {
        locationManager.removeUpdates(locationListener)
    }

    private val locationPaint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        mLocation?.let {
            val result = WGS84ToCGS2000(it.longitude, it.latitude)
            val point = transformer.geoToScreen(result[1], result[0])
            canvas.drawCircle(point[0].toFloat(), point[1].toFloat(), 8f, locationPaint)
            canvas.drawText(
                "(${it.longitude},${it.latitude})",
                (point[0] + 10).toFloat(),
                (point[1] - 10).toFloat(),
                locationPaint
            )

        }
    }

    fun WGS84ToCGS2000(longitude: Double, latitude: Double): DoubleArray //参数 经度，纬度
    {
        var pt: DoubleArray
        val output = DoubleArray(2)
        val longitude1: Double
        val latitude1: Double
        var longitude0: Double
        val Y0: Double
        var xval: Double
        var yval: Double
        //NN曲率半径，测量学里面用N表示
        //M为子午线弧长，测量学里用大X表示
        //fai为底点纬度，由子午弧长反算公式得到，测量学里用Bf表示
        //R为底点所对的曲率半径，测量学里用Nf表示
        val f: Double
        val e2: Double
        val ee: Double
        val NN: Double
        val T: Double
        val C: Double
        val A: Double
        val M: Double
        val iPI: Double
        iPI = 0.0174532925199433 //3.1415926535898/180.0;
        val a: Double = 6378137.0
        f = 1 / 298.257222101 //CGCS2000坐标系参数
        //a=6378137.0; f=1/298.2572236; //wgs84坐标系参数
        longitude0 = 117.0 //中央子午线 根据实际进行配置
        longitude0 *= iPI //中央子午线转换为弧度
        longitude1 = longitude * iPI //经度转换为弧度
        latitude1 = latitude * iPI //纬度转换为弧度
        e2 = 2 * f - f * f
        ee = e2 * (1.0 - e2)
        NN = a / Math.sqrt(1.0 - e2 * Math.sin(latitude1) * Math.sin(latitude1))
        T = Math.tan(latitude1) * Math.tan(latitude1)
        C = ee * Math.cos(latitude1) * Math.cos(latitude1)
        A = (longitude1 - longitude0) * Math.cos(latitude1)
        M =
            a * ((1 - e2 / 4 - 3 * e2 * e2 / 64 - 5 * e2 * e2 * e2 / 256) * latitude1 - (3 * e2 / 8 + 3 * e2 * e2 / 32 + (45 * e2 * e2
                    * e2) / 1024) * Math.sin(2 * latitude1)
                    + (15 * e2 * e2 / 256 + 45 * e2 * e2 * e2 / 1024) * Math.sin(4 * latitude1) - 35 * e2 * e2 * e2 / 3072 * Math.sin(
                6 * latitude1
            ))
        xval =
            NN * (A + (1 - T + C) * A * A * A / 6 + (5 - 18 * T + T * T + 72 * C - 58 * ee) * A * A * A * A * A / 120)
        yval =
            M + NN * Math.tan(latitude1) * (A * A / 2 + (5 - T + 9 * C + 4 * C * C) * A * A * A * A / 24 + (61 - 58 * T + T * T + 600 * C - 330 * ee) * A * A * A * A * A * A / 720)
        val X0: Double = 500000.0
        Y0 = 0.0
        xval += X0
        yval += Y0

        //转换为投影
        output[0] = xval
        output[1] = yval
        pt = doubleArrayOf(output[0], output[1])
        return pt
    }
}