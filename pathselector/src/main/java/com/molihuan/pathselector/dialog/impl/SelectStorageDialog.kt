package com.molihuan.pathselector.dialog.impl

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.molihuan.pathselector.dialog.BaseDialog
import com.molihuan.pathselector.entity.StorageBean
import com.molihuan.pathselector.adapter.StorageListAdapter
import com.molihuan.pathselector.utils.ReflectTools
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.molihuan.pathselector.R
import com.molihuan.pathselector.databinding.GeneralTitleRecyviewBinding


class SelectStorageDialog(context: Context) : BaseDialog(context), OnItemClickListener {
    private lateinit var binding: GeneralTitleRecyviewBinding

    private var storageList: MutableList<StorageBean>? = null
    private var storageListAdapter: StorageListAdapter? = null

    //选择的StorageBean
    private var selectedStorage: StorageBean? = null
    override fun setContentView(): View {
        binding = GeneralTitleRecyviewBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initData() {
        super.initData()

        //存储数据
        storageList = mutableListOf()
        //通过反射的方式得到所有的存储路径（内部存储+外部存储）
        val allStoragePath = ReflectTools.getAllStoragePath(mContext)
        for (storagePath in allStoragePath) {
            storageList!!.add(StorageBean(storagePath, false))
        }

        //设置数据和监听
        binding.recyviewGeneralTitleRecyview.layoutManager = LinearLayoutManager(mContext)
        storageListAdapter = StorageListAdapter(R.layout.general_item_tv, storageList)
        binding.recyviewGeneralTitleRecyview.adapter = storageListAdapter
    }

    override fun initView() {
        setCanceledOnTouchOutside(false)
        window!!.setLayout(mConfigData.pathSelectDialogWidth!! * 92 / 100, ViewGroup.LayoutParams.WRAP_CONTENT)
        binding.titleGeneralTitleRecyview.setText(R.string.tip_dialog_title_select_memory_path)
        binding.confirmGeneralTitleRecyview.setText(R.string.option_confirm)
        binding.cancelGeneralTitleRecyview.setText(R.string.option_cancel)
    }

    override fun setListeners() {
        storageListAdapter!!.setOnItemClickListener(this)
        binding.confirmGeneralTitleRecyview.setOnClickListener(this)
        binding.cancelGeneralTitleRecyview.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.confirm_general_title_recyview) {
            //确定按钮
            if (selectedStorage != null) {
                //改变初始路径
//                psf.setInitPath(selectedStorage.getRootPath());
                //刷新
                psf!!.updateFileList(selectedStorage!!.rootPath)
                //刷新面包屑
                psf!!.updateTabbarList()
            }
            dismiss()
        } else if (id == R.id.cancel_general_title_recyview) {
            //取消按钮
            dismiss()
        }
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        if (adapter is StorageListAdapter) {
            //不为null说明已经选择了，则把当前选择的item设置未选中
            if (selectedStorage != null) {
                selectedStorage!!.selected = false
            }
            //获取当前点击新的item，并设置选中
            selectedStorage = storageList!![position]
            selectedStorage!!.selected = true
            //刷新
            storageListAdapter!!.notifyDataSetChanged()
        }
    }
}