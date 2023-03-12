package com.molihuan.pathselector.adapter

import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.molihuan.pathselector.listener.CommonItemListener
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.molihuan.pathselector.R


class MorePopupAdapter(layoutResId: Int, data: MutableList<CommonItemListener>?) :
    BaseQuickAdapter<CommonItemListener, BaseViewHolder>(layoutResId, data) {
    override fun convert(holder: BaseViewHolder, item: CommonItemListener) {
        val fontBean = item.fontBean
        val container = holder.getView<RelativeLayout>(R.id.general_item_relatl_container)
        val leftIco = holder.getView<ImageView>(R.id.general_item_imav_ico)
        val tv = holder.getView<TextView>(R.id.general_item_textview)
        tv.text = fontBean.text
        //如果已经设置了样式就不设置了
        if (item.setViewStyle(container, leftIco, tv)) {
            return
        }
        if (fontBean.leftIcoResId != null) {
            leftIco.setImageResource(fontBean.leftIcoResId)
            leftIco.visibility = View.VISIBLE
        }
        tv.setTextColor(fontBean.color)
        tv.textSize = fontBean.size.toFloat()
    }
}