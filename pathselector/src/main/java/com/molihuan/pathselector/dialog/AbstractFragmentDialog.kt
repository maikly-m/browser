package com.molihuan.pathselector.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.DialogFragment
import com.molihuan.pathselector.interfaces.IActivityAndFragment
import com.molihuan.pathselector.dao.SelectConfigData
import com.molihuan.pathselector.service.impl.ConfigDataBuilderImpl
import com.molihuan.pathselector.fragment.impl.PathSelectFragment

abstract class AbstractFragmentDialog : DialogFragment(), DialogInterface.OnKeyListener {
    //FragmentView
    var mFragmentView: View? = null

    private var mDialog: Dialog? = null

    //宽
    private var mWidth = 0

    //高
    private var mHeight = 0
    @JvmField
    protected var mConfigData: SelectConfigData = ConfigDataBuilderImpl.getInstance().selectConfigData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (mFragmentView == null) {
            //获取dialog
            mDialog = dialog
            //初始化宽高
            mWidth = mConfigData.pathSelectDialogWidth!!
            mHeight = mConfigData.pathSelectDialogHeight!!

            //获取Fragment布局
            mFragmentView = inflater.inflate(setFragmentViewId(), container, false)
            //获取组件
            getComponents(mFragmentView)
            //初始化数据
            initData()
            //初始化视图
            initView()
            //设置监听
            setListeners()
        }
        return mFragmentView
    }

    /**
     * 子类的数据初始化必须在这些方法中，否则可能出现空指针异常
     *
     * @param
     */
    abstract fun setFragmentViewId(): Int
    abstract fun getComponents(view: View?)
    abstract fun initData()
    @CallSuper
    open fun initView() {
        if (mDialog != null) {
            //点击外面不能取消
            mDialog!!.setCanceledOnTouchOutside(false)
        }
    }

    @CallSuper
    fun setListeners() {
        if (mDialog != null) {
            //添加监听
            mDialog!!.setOnKeyListener(this)
        }
    }

    abstract val pathSelectFragment: PathSelectFragment?

    /**
     * 子类可以重写此方法让fragment先处理返回按钮事件
     * keyCode == KeyEvent.KEYCODE_BACK(返回键)
     * true表示Fragment已经处理了Activity可以不用处理了 false反之
     */
    override fun onKey(dialog: DialogInterface, keyCode: Int, event: KeyEvent): Boolean {
        //会有两次调用按下和松开先消费掉一次
        if (event.action != KeyEvent.ACTION_DOWN) {
            return true
        }
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            //此处捕获back操作，如果不希望所在的Activity监听到back键，需要返回true，消费掉。
            dismissAllowingStateLoss()
            true
        } else {
            //这里注意当不是返回键时需将事件扩散，否则无法处理其他点击事件
            false
        }
    }

    /**
     * 设置宽高
     */
    private fun setWidthHeight() {
        if (mDialog != null) {
            if (mDialog!!.window != null) {
                mDialog!!.window!!.setLayout(mWidth, mHeight)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        setWidthHeight()
    }


}