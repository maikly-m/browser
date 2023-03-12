package com.molihuan.pathselector.controller.impl

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.molihuan.pathselector.PathSelector.fragment
import com.molihuan.pathselector.controller.AbstractBuildController
import com.molihuan.pathselector.fragment.impl.PathSelectFragment
import com.molihuan.pathselector.PathSelector
import com.molihuan.pathselector.utils.Mtools
import com.molihuan.pathselector.utils.FragmentTools
import com.molihuan.pathselector.utils.MConstants
import com.molihuan.pathselector.dialog.AbstractFragmentDialog
import java.lang.ClassCastException
import java.lang.NullPointerException

class FragmentController : AbstractBuildController() {
    override var pathSelectFragment: PathSelectFragment? = null
        private set

    override fun show(): PathSelectFragment? {
        val frameLayoutId = mConfigData.frameLayoutId
            ?: throw NullPointerException("frameLayoutId is a null object reference and you must set it")
        val context = mConfigData.context
        val fragment = fragment
        val fragmentManager: FragmentManager = fragment?.childFragmentManager ?: if (context is FragmentActivity) {
                context.supportFragmentManager
            } else {
                throw ClassCastException("context必须为FragmentActivity类型以及其子类(如 AppCompatActivity)或PathSelector.fragment不为空")
            }
        mConfigData.fragmentManager = fragmentManager
        Mtools.log("PathSelectFragment  new  start")
        pathSelectFragment = PathSelectFragment()
        Mtools.log("PathSelectFragment  new  end")
        Mtools.log("PathSelectFragment  show  start")
        FragmentTools.fragmentReplace(
            fragmentManager,
            frameLayoutId,
            pathSelectFragment,
            MConstants.TAG_ACTIVITY_FRAGMENT
        )
        Mtools.log("PathSelectFragment  show  end")
        return pathSelectFragment
    }

    override val dialogFragment: AbstractFragmentDialog?
        get() = null
}