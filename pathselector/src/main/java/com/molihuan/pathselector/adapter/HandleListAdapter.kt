package com.molihuan.pathselector.adapter

import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.molihuan.pathselector.listener.CommonItemListener
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.chad.library.adapter.base.module.LoadMoreModule
import com.molihuan.pathselector.R

/**
 * @ClassName: HandleAdapter
 * @Author: molihuan
 * @Date: 2022/11/22/21:55
 * @Description:
 */
class HandleListAdapter private constructor(layoutResId: Int, data: MutableList<CommonItemListener>?) :
    BaseQuickAdapter<CommonItemListener, BaseViewHolder>(layoutResId, data), LoadMoreModule {
    private var itemWidth = 0

    constructor(id: Int, data: MutableList<CommonItemListener>?, itemWidth: Int) : this(id, data) {
        this.itemWidth = itemWidth
    }

    override fun convert(holder: BaseViewHolder, item: CommonItemListener) {
        val relatl = holder.getView<RelativeLayout>(R.id.item_handle_relatl)
        val leftIco = holder.getView<ImageView>(R.id.item_handle_imav_ico)
        val tv = holder.getView<TextView>(R.id.item_handle_tv)
        val fontBean = item.fontBean
        //设置宽度
        relatl.layoutParams.width = itemWidth
        tv.text = fontBean.text

        //如果已经设置了样式就不设置了
        if (item.setViewStyle(relatl, leftIco, tv)) {
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