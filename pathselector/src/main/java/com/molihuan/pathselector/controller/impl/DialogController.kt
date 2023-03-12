package com.molihuan.pathselector.controller.impl

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.molihuan.pathselector.PathSelector.fragment
import com.molihuan.pathselector.controller.AbstractBuildController
import com.molihuan.pathselector.dialog.AbstractFragmentDialog
import com.molihuan.pathselector.fragment.impl.PathSelectFragment
import com.molihuan.pathselector.PathSelector
import com.molihuan.pathselector.dialog.impl.PathSelectDialog
import com.molihuan.pathselector.utils.MConstants
import java.lang.ClassCastException

class DialogController : AbstractBuildController() {
    override var dialogFragment: AbstractFragmentDialog? = null
        private set

    override fun show(): PathSelectFragment? {
        val context = mConfigData.context
        val fragmentManager: FragmentManager = fragment?.childFragmentManager ?: if (context is FragmentActivity) {
                context.supportFragmentManager
            } else {
                throw ClassCastException("context必须为FragmentActivity类型以及其子类(如 AppCompatActivity)或PathSelector.fragment不为空")
            }
        mConfigData.fragmentManager = fragmentManager
        dialogFragment = PathSelectDialog()

        //显示 Dialog 弹窗
        dialogFragment!!.show(fragmentManager, MConstants.TAG_ACTIVITY_FRAGMENT) //这里设置tag没有用需要在AbstractDialog中设置tag
        return dialogFragment!!.pathSelectFragment
    }

    override val pathSelectFragment: PathSelectFragment
        get() = dialogFragment!!.pathSelectFragment
}