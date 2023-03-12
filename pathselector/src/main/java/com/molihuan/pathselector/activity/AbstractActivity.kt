package com.molihuan.pathselector.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.molihuan.pathselector.interfaces.IActivityAndFragment
import com.molihuan.pathselector.dao.SelectConfigData
import com.molihuan.pathselector.service.impl.ConfigDataBuilderImpl

abstract class AbstractActivity : AppCompatActivity(), IActivityAndFragment {
    @JvmField
    protected var mConfigData: SelectConfigData = ConfigDataBuilderImpl.getInstance().selectConfigData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //设置布局资源
        this.setContentView(setContentView())
        //初始化数据
        initData()
        //初始化视图
        initView()
        //设置监听
        setListeners()
    }

    abstract fun setContentView(): View

    open fun initData() {}
    open fun initView() {
        hideActionBar()
    }

    open fun setListeners() {}
    private fun hideActionBar() {
        supportActionBar?.hide()
    }

    /**
     * 与Fragment通讯接口
     *
     * @param data
     * @return
     */
    override fun invoke(data: Map<*, *>?): Map<*, *>? {
        return null
    }

}