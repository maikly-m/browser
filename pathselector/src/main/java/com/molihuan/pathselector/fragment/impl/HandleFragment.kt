package com.molihuan.pathselector.fragment.impl

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.molihuan.pathselector.fragment.AbstractHandleFragment
import com.molihuan.pathselector.listener.CommonItemListener
import com.molihuan.pathselector.adapter.HandleListAdapter
import com.molihuan.pathselector.entity.FontBean
import com.molihuan.pathselector.utils.MConstants
import com.blankj.molihuan.utilcode.util.SizeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.chad.library.adapter.base.listener.OnItemLongClickListener
import com.molihuan.pathselector.R
import com.molihuan.pathselector.databinding.FragmentHandleBinding
import java.util.ArrayList

class HandleFragment : AbstractHandleFragment(), OnItemClickListener, OnItemLongClickListener {
    private lateinit var binding: FragmentHandleBinding

    private var handleItemTv: TextView? = null
    override val handleItemListeners : MutableList<CommonItemListener> = mutableListOf()
    override var handleListAdapter  : HandleListAdapter? = null
    private var fontBean : FontBean? = null
    private var isDialogBuild  = false

    override fun setFragmentView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentHandleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initData() {
        super.initData()

        //将监听回调列表转换为数组
        fontBean = mConfigData.handleItemListeners!![0].fontBean //只需要一份样式
        if (mConfigData.handleItemListeners != null) {
            for (listener in mConfigData.handleItemListeners!!) {
                handleItemListeners.add(listener)
            }
        }
        if (mConfigData.buildType == MConstants.BUILD_DIALOG) {
            isDialogBuild = true
        }
    }

    override fun initView() {
        //通过回调的方法获取mRecView宽度并设置其item宽度并设置数据适配器
        SizeUtils.forceGetViewSize(binding.recvHandle) { view -> //计算 mRecView item宽度
            val width = view.measuredWidth / handleItemListeners.size
            //设置适配器
            binding.recvHandle.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            //TODO  Arrays.asList 返回的类型不是 java.util.ArrayList 而是 java.util.Arrays.ArrayList 返回的 ArrayList 对象是只读的
            handleListAdapter = HandleListAdapter(R.layout.item_handle, handleItemListeners, width)
            binding.recvHandle.adapter = handleListAdapter
            handleListAdapter!!.setOnItemClickListener(this@HandleFragment)
            handleListAdapter!!.setOnItemLongClickListener(this@HandleFragment)
        }
    }

    override fun setListeners() {}

    @SuppressLint("NotifyDataSetChanged")
    override fun refreshHandleList() {
        handleListAdapter!!.notifyDataSetChanged()
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, v: View, position: Int) {
        if (adapter is HandleListAdapter) {
            handleItemTv = v.findViewById(R.id.item_handle_tv)
            optionItemClick(v, handleItemTv, position)
        }
    }

    /**
     * 点击option回调
     *
     * @param v 点击的视图
     * @param i 点击的索引
     */
    private fun optionItemClick(v: View?, tv: TextView?, i: Int) {
        handleItemListeners[i].onClick(
            v,
            tv,
            psf!!.selectedFileList,
            psf!!.currentPath,
            psf
        )
    }

    override fun onItemLongClick(adapter: BaseQuickAdapter<*, *>, v: View, position: Int): Boolean {
        if (adapter is HandleListAdapter) {
            handleItemTv = v.findViewById(R.id.item_handle_tv)
            return optionItemLongClick(v, handleItemTv, position)
        }
        return false
    }

    /**
     * 长按option回调
     *
     * @param v 点击的视图
     * @param i 点击的索引
     */
    private fun optionItemLongClick(v: View?, tv: TextView?, i: Int): Boolean {
        return handleItemListeners[i].onLongClick(
            v,
            tv,
            psf!!.selectedFileList,
            psf!!.currentPath,
            psf
        )
    }
}