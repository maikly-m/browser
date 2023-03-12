package com.molihuan.pathselector.fragment

import androidx.annotation.CallSuper
import com.molihuan.pathselector.fragment.AbstractFragment
import com.molihuan.pathselector.interfaces.IFileShowFragment
import com.molihuan.pathselector.fragment.BasePathSelectFragment
import com.molihuan.pathselector.utils.MConstants

abstract class AbstractFileShowFragment : AbstractFragment(), IFileShowFragment {
    @JvmField
    protected var psf //总fragment
            : BasePathSelectFragment? = null

    @CallSuper
    override fun initData() {
        psf = mConfigData.fragmentManager!!.findFragmentByTag(MConstants.TAG_ACTIVITY_FRAGMENT) as BasePathSelectFragment?
    }
}