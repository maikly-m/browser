package com.molihuan.pathselector.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.molihuan.pathselector.dao.SelectConfigData
import com.molihuan.pathselector.service.impl.ConfigDataBuilderImpl

abstract class AbstractFragment : DialogFragment() {
    //FragmentView
    protected var mFragmentView: View? = null
    @JvmField
    protected var mConfigData: SelectConfigData = ConfigDataBuilderImpl.getInstance().selectConfigData
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (mFragmentView == null) {
            //获取Fragment布局
            mFragmentView = setFragmentView(inflater, container)
            //初始化数据
            initData()
            //初始化视图
            initView()
            //设置监听
            setListeners()
        }
        return mFragmentView
    }

    abstract fun setFragmentView(inflater: LayoutInflater, container: ViewGroup?): View
    open fun initData() {}
    open fun initView() {}
    open fun setListeners() {}

    /**
     * 子类可以重写此方法让fragment先处理返回按钮事件
     *
     * @return true表示Fragment已经处理了Activity可以不用处理了 false反之
     */
    open fun onBackPressed(): Boolean {
        return false
    }

}