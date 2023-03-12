package com.molihuan.pathselector.interfaces

import android.widget.TextView
import com.molihuan.pathselector.adapter.MorePopupAdapter
import com.molihuan.pathselector.listener.CommonItemListener

/**
 * @ClassName: AbstractTitlebarFragment
 * @Author: molihuan
 * @Date: 2022/11/22/17:37
 * @Description: 标题区域 Fragment
 */
interface ITitlebarFragment {
    val morePopupAdapter: MorePopupAdapter?
    val morePopupItemListeners: List<CommonItemListener?>?

    /**
     * 刷新 MorePopup ui
     *
     * @return
     */
    fun refreshMorePopup()

    /**
     * 此方法只适用仅仅设置了一个morePopup即setMorePopupItemListeners()中只设置了一个
     * 当只设置了一个MorePopupItem就会返回这个设置的TextView
     * 当设置了多个则返回的TextView是隐藏的不建议操作
     *
     * @return
     */
    val onlyOneMorePopupTextView: TextView?
}