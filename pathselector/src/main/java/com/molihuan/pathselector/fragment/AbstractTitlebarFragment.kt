package com.molihuan.pathselector.fragment

import android.widget.TextView
import androidx.annotation.CallSuper
import com.molihuan.pathselector.fragment.AbstractFragment
import com.molihuan.pathselector.interfaces.ITitlebarFragment
import com.molihuan.pathselector.fragment.BasePathSelectFragment
import com.molihuan.pathselector.utils.MConstants
import com.molihuan.pathselector.adapter.MorePopupAdapter
import com.molihuan.pathselector.listener.CommonItemListener

/**
 * @ClassName: AbstractTitlebarFragment
 * @Author: molihuan
 * @Date: 2022/11/22/17:37
 * @Description: 标题区域 Fragment
 */
abstract class AbstractTitlebarFragment : AbstractFragment(), ITitlebarFragment {
    @JvmField
    protected var psf //总fragment
            : BasePathSelectFragment? = null

    @CallSuper
    override fun initData() {
        psf = mConfigData.fragmentManager!!.findFragmentByTag(MConstants.TAG_ACTIVITY_FRAGMENT) as BasePathSelectFragment?
    }

    override val morePopupAdapter: MorePopupAdapter?
        get() = null
    override val morePopupItemListeners: List<CommonItemListener>?
        get() = null

    override fun refreshMorePopup() {}
    override val onlyOneMorePopupTextView: TextView?
        get() = null
}