package com.example.mylibrary

import android.content.res.Resources
import java.text.SimpleDateFormat
import java.util.*

fun dp2px(dp: Float): Int {
    val scale = Resources.getSystem().displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}

fun formatTime(long: Long): String {
    if (long > 60*60*1000) {
        SimpleDateFormat("hh:mm:ss", Locale.ROOT).let {
            return it.format(long)
        }
    } else {
        SimpleDateFormat("mm:ss", Locale.ROOT).let {
            return it.format(long)
        }
    }
}

