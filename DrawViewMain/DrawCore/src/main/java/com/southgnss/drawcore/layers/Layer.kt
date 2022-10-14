package com.southgnss.drawcore.layers

import com.southgnss.drawcore.CustomDrawView
import com.southgnss.drawcore.IDrawable
import com.southgnss.drawcore.ViewTransformer

/**
 * Created by weipi on 2022/4/12.
 */
abstract class Layer : IDrawable {
    protected lateinit var transformer: ViewTransformer
    protected lateinit var mapView: CustomDrawView

    var layerName = "Default"

    internal open fun register(transformer: ViewTransformer, mapView: CustomDrawView) {
        this.transformer = transformer;
        this.mapView = mapView
    }
}