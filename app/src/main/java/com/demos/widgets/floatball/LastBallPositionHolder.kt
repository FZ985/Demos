package com.demos.widgets.floatball

import android.graphics.PointF


/**
 * by DAD ZZ
 * 2025/8/18
 * desc：
 **/
object LastBallPositionHolder {
    private var pos: PointF? = null
    fun save(x: Float, y: Float) {
        pos = PointF(x, y)
    }

    fun load(): PointF? = pos
}