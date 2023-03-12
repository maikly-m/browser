package com.molihuan.pathselector.fragment

import androidx.annotation.CallSuper
import com.molihuan.pathselector.fragment.AbstractFragment
import com.molihuan.pathselector.interfaces.ITabbarFragment
import com.molihuan.pathselector.fragment.BasePathSelectFragment
import com.molihuan.pathselector.utils.MConstants
import com.molihuan.pathselector.adapter.TabbarListAdapter
import com.molihuan.pathselector.entity.TabbarFileBean

/**
 * @ClassName: AbstractTabbarFragment
 * @Author: molihuan
 * @Date: 2022/11/22/17:38
 * @Description: 面包屑Fragment
 */
abstract class AbstractTabbarFragment : AbstractFragment(), ITabbarFragment {
    @JvmField
    protected var psf //总fragment
            : BasePathSelectFragment? = null

    @CallSuper
    override fun initData() {
        psf = mConfigData.fragmentManager!!.findFragmentByTag(MConstants.TAG_ACTIVITY_FRAGMENT) as BasePathSelectFragment?
    }

    override val tabbarListAdapter: TabbarListAdapter?
        get() = null
    override val tabbarList: List<TabbarFileBean>?
        get() = null

    override fun updateTabbarList(): List<TabbarFileBean?>? {
        return null
    }

    override fun refreshTabbarList() {}
}