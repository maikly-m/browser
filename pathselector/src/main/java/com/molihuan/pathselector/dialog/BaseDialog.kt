package com.molihuan.pathselector.dialog

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import com.molihuan.pathselector.dao.SelectConfigData
import com.molihuan.pathselector.service.impl.ConfigDataBuilderImpl
import com.molihuan.pathselector.fragment.BasePathSelectFragment
import com.molihuan.pathselector.utils.MConstants
import com.molihuan.pathselector.dialog.BaseDialog

abstract class BaseDialog : AlertDialog, View.OnClickListener {
    @JvmField
    protected var mContext: Context
    @JvmField
    protected var mConfigData: SelectConfigData = ConfigDataBuilderImpl.getInstance().selectConfigData
    @JvmField
    protected var psf //总fragment
            : BasePathSelectFragment? = null

    constructor(context: Context) : super(context) {
        mContext = context
    }

    constructor(context: Context, themeResId: Int) : super(context, themeResId) {
        mContext = context
    }

    constructor(context: Context, cancelable: Boolean, cancelListener: DialogInterface.OnCancelListener?) : super(
        context,
        cancelable,
        cancelListener
    ) {
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //设置布局资源
        setContentView(setContentView())
        //获取组件
        //初始化数据
        initData()
        //初始化视图
        initView()
        //设置监听
        setListeners()
    }

    abstract fun setContentView(): View
    open fun initData() {
        psf = mConfigData.fragmentManager!!.findFragmentByTag(MConstants.TAG_ACTIVITY_FRAGMENT) as BasePathSelectFragment?
    }

    open fun initView() {}
    open fun setListeners() {}
    override fun onClick(v: View) {}

    /**
     * 回调接口
     */
    interface IOnConfirmListener {
        fun onClick(v: View?, dialog: BaseDialog?): Boolean
    }

    interface IOnCancelListener {
        fun onClick(v: View?, dialog: BaseDialog?): Boolean
    }
}