package com.molihuan.pathselector.adapter

import android.graphics.Color
import android.widget.TextView
import com.molihuan.pathselector.entity.StorageBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.molihuan.pathselector.R

class StorageListAdapter(layoutResId: Int, data: MutableList<StorageBean>?) :
    BaseQuickAdapter<StorageBean, BaseViewHolder>(layoutResId, data) {
    override fun convert(holder: BaseViewHolder, item: StorageBean) {
        val tv = holder.getView<TextView>(R.id.general_item_textview)
        tv.text = item.rootPath
        if (item.selected) {
            tv.setTextColor(Color.rgb(255, 165, 0))
        } else {
            tv.setTextColor(Color.GRAY)
        }
    }
}