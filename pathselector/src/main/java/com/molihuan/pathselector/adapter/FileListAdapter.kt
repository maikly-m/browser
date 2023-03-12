package com.molihuan.pathselector.adapter

import android.annotation.SuppressLint
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.blankj.molihuan.utilcode.util.StringUtils
import com.blankj.molihuan.utilcode.util.TimeUtils
import com.molihuan.pathselector.entity.FileBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.chad.library.adapter.base.module.LoadMoreModule
import com.molihuan.pathselector.R
import com.molihuan.pathselector.dao.SelectConfigData
import com.molihuan.pathselector.service.impl.ConfigDataBuilderImpl
import com.molihuan.pathselector.utils.MConstants

open class FileListAdapter(layoutResId: Int, data: MutableList<FileBean>?)
    : BaseQuickAdapter<FileBean, BaseViewHolder>(layoutResId, data), LoadMoreModule {
    protected var mConfigData: SelectConfigData = ConfigDataBuilderImpl.getInstance().selectConfigData
    private val fileShowFragment = mConfigData.fileShowFragment
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseViewHolder, item: FileBean) {
        val linlContainer = holder.getView<LinearLayout>(R.id.linl_item_file_container) //总容器
        val imgvIco = holder.getView<ImageView>(R.id.imgv_item_file_ico) //文件图标
        val imgvEnter = holder.getView<ImageView>(R.id.imgv_item_file_enter) //右边进入图片
        val tvName = holder.getView<TextView>(R.id.tv_item_file_name) //文件名称
        val tvDetail = holder.getView<TextView>(R.id.tv_item_file_detail) //文件详细信息
        val checkBox = holder.getView<CheckBox>(R.id.checkbox_item_file_choose) //多选框
        if (item.path == null) {
            //说明是缓存filebean
            linlContainer.visibility = View.GONE //隐藏总容器
            return
        } else if (item.size == MConstants.FILEBEAN_BACK_FLAG) {
            linlContainer.visibility = View.VISIBLE
            //说明是返回fileBean
            imgvIco.setImageResource(item.fileIcoType)
            imgvEnter.visibility = View.INVISIBLE
            tvName.text = item.name
            tvDetail.text = ""
            checkBox.visibility = View.INVISIBLE
            checkBox.isChecked = false
        } else {
            linlContainer.visibility = View.VISIBLE
            //正常filebean
            imgvIco.setImageResource(item.fileIcoType)

            //如果是文件夹且当前不是多选模式则设置可以进入的图标
            if (item.isDir && !fileShowFragment!!.isMultipleSelectionMode) {
                imgvEnter.visibility = View.VISIBLE
            } else {
                imgvEnter.visibility = View.INVISIBLE
            }
            //如果CheckBox需要显示则显示
            if (item.boxVisible) {
                checkBox.visibility = View.VISIBLE
            } else {
                checkBox.visibility = View.INVISIBLE
            }
            checkBox.isChecked = item.boxChecked
            tvName.text = item.name
            //文件文件夹时间----大小时间
            if (item.isDir) {
                val dirDetail = String.format(
                    StringUtils.getString(R.string.filebeanitem_dir_detail),
                    item.childrenDirNumber,
                    item.childrenFileNumber
                )
                tvDetail.text = dirDetail
            } else {
                tvDetail.text =
                    TimeUtils.millis2String(item.modifyTime, "yy-MM-dd HH:mm  ") + item.sizeString
            }
        }
    }
}