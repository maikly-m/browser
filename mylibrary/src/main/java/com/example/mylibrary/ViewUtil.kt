package com.example.mylibrary

import android.content.res.Resources

fun dp2px(dp: Float): Int {
    val scale = Resources.getSystem().displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}

