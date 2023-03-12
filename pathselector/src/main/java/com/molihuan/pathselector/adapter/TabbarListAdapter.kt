package com.molihuan.pathselector.adapter

import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.molihuan.pathselector.entity.TabbarFileBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.chad.library.adapter.base.module.LoadMoreModule
import com.molihuan.pathselector.R

class TabbarListAdapter(layoutResId: Int, data: MutableList<TabbarFileBean>?) :
    BaseQuickAdapter<TabbarFileBean, BaseViewHolder>(layoutResId, data), LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: TabbarFileBean) {
        val relatContainer = holder.getView<RelativeLayout>(R.id.relatl_item_tabbar)
        if (item.path == null) {
            relatContainer.visibility = View.GONE
        } else {
            relatContainer.visibility = View.VISIBLE
        }
        val tv = holder.getView<TextView>(R.id.tv_item_tabbar)
        tv.text = item.name
    }
}