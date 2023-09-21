package com.demos

import android.content.Context
import android.content.res.Resources
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat


/**
 *  author : JFZ
 *  date : 2023/7/29 16:33
 *  description : 扩展函数
 */

val Number.dp
    get() = (this.toFloat() * Resources.getSystem().displayMetrics.density).toInt()

val Number.sp
    get() = (this.toFloat() * Resources.getSystem().displayMetrics.scaledDensity + 0.5f).toInt()

fun Context.getColorCompat(@ColorRes colorRes: Int) = ContextCompat.getColor(this, colorRes)

fun Context.getDrawableCompat(@DrawableRes drawableRes: Int) =
    ContextCompat.getDrawable(this, drawableRes)

