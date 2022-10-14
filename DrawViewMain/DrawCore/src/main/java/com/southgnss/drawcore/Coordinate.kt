package com.southgnss.drawcore

/**
 * Created by weipi on 2022/4/13.
 */
class Coordinate {
    var x: Double = 0.0
    var y: Double = 0.0
    var z: Double = 0.0

    constructor(x: Double, y: Double, z: Double) {
        this.x = x
        this.y = y
        this.z = z
    }

    constructor(x: Double, y: Double) {
        this.x = x
        this.y = y
    }


}