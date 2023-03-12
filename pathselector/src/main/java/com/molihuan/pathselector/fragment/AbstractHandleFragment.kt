package com.molihuan.pathselector.fragment

import androidx.annotation.CallSuper
import com.molihuan.pathselector.fragment.AbstractFragment
import com.molihuan.pathselector.interfaces.IHandleFragment
import com.molihuan.pathselector.fragment.BasePathSelectFragment
import com.molihuan.pathselector.utils.MConstants
import com.molihuan.pathselector.listener.CommonItemListener
import com.molihuan.pathselector.adapter.HandleListAdapter

abstract class AbstractHandleFragment : AbstractFragment(), IHandleFragment {
    @JvmField
    protected var psf //总fragment
            : BasePathSelectFragment? = null

    @CallSuper
    override fun initData() {
        psf = mConfigData.fragmentManager!!.findFragmentByTag(MConstants.TAG_ACTIVITY_FRAGMENT) as BasePathSelectFragment?
    }

    override val handleItemListeners: MutableList<CommonItemListener>?
        get() = null
    override val handleListAdapter: HandleListAdapter?
        get() = null

    override fun refreshHandleList() {}
}