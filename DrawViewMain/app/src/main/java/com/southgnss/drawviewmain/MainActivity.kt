package com.southgnss.drawviewmain

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import com.southgnss.drawcore.Coordinate
import com.southgnss.drawcore.CustomDrawView
import com.southgnss.drawcore.layers.PointLayer
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var locationLayer: LocationLayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val customDrawView = findViewById<CustomDrawView>(R.id.drawView)
        customDrawView.setData()

        val btn = findViewById<Button>(R.id.btnTest)
        btn.setOnClickListener { customDrawView.zoomAll() }

        val pointLayer = PointLayer()
        pointLayer.layerName = "Point"
        val random = Random(50)
        for (i in 0..100000) {
            pointLayer.add(
                Coordinate(
                    2569169.885710221 + random.nextDouble(1.0, 300.0),
                    132980.60698954226 + random.nextDouble(1.0, 300.0)
                )
            )
        }
        pointLayer.add(
            Coordinate(
                15.0,
                15.0
            )
        )
        pointLayer.add(
            Coordinate(
                10.0,
                10.0
            )
        )
        customDrawView.addLayer(pointLayer)

        locationLayer = LocationLayer()

        findViewById<CheckBox>(R.id.cbCurrentLocation).setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                locationLayer.startLocationUpdates(this)
            }else{
                locationLayer.stopLocationUpdates()
            }
        }
        customDrawView.addLayer(locationLayer)
    }

    override fun onResume() {
        super.onResume()
        if (findViewById<CheckBox>(R.id.cbCurrentLocation).isChecked) {
            locationLayer.startLocationUpdates(this)
        }
    }

    override fun onPause() {
        super.onPause()
        locationLayer.stopLocationUpdates()
    }
}