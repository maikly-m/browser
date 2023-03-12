package com.molihuan.pathselector.entity

import android.graphics.Color
import androidx.annotation.DrawableRes
import java.io.Serializable
import kotlin.jvm.JvmOverloads

/**
 * @ClassName: FontBean
 * @Author: molihuan
 * @Date: 2022/11/26/12:39
 * @Description: 字---样式实体
 */
class FontBean @JvmOverloads constructor(//文本
    var text: CharSequence, //大小
    var size: Int = 18, //颜色
    var color: Int = Color.BLACK, //左边图片资源id
    @param:DrawableRes var leftIcoResId: Int? = null
) : Serializable {

    fun setText(text: String) {
        this.text = text
    }
}