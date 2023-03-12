package com.molihuan.pathselector.controller.impl

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.molihuan.pathselector.PathSelector.fragment
import com.molihuan.pathselector.controller.AbstractBuildController
import com.molihuan.pathselector.fragment.impl.PathSelectFragment
import com.molihuan.pathselector.activity.impl.PathSelectActivity
import com.molihuan.pathselector.PathSelector
import com.molihuan.pathselector.dialog.AbstractFragmentDialog
import java.lang.NullPointerException


class ActivityController : AbstractBuildController() {
    override fun show(): PathSelectFragment? {
        val requestCode = mConfigData.requestCode ?: throw NullPointerException("requestCode is a null object reference and you must set it")
        //判断是否设置了请求码
        val activity = mConfigData.context ?: return null
        val intent = Intent(activity, PathSelectActivity::class.java)
        if (fragment != null) {
            fragment!!.startActivityForResult(intent, requestCode) //设置返回码
        } else {
            if (activity is FragmentActivity) {
                activity.startActivityForResult(intent, requestCode)
            } else {
                (activity as Activity).startActivityForResult(intent, requestCode)
            }
        }
        return null
    }

    //都跳转了，就没有了
    override val pathSelectFragment: PathSelectFragment?
        get() = null
    override val dialogFragment: AbstractFragmentDialog?
        get() = null
}