package com.molihuan.pathselector.dialog.impl

import android.content.DialogInterface
import android.view.KeyEvent
import android.view.View
import com.molihuan.pathselector.R
import com.molihuan.pathselector.dialog.AbstractFragmentDialog
import com.molihuan.pathselector.fragment.impl.PathSelectFragment
import com.molihuan.pathselector.utils.Mtools
import com.molihuan.pathselector.utils.FragmentTools
import com.molihuan.pathselector.utils.MConstants


class PathSelectDialog : AbstractFragmentDialog() {
    override var pathSelectFragment: PathSelectFragment? = null
        private set

    override fun setFragmentViewId(): Int {
        return R.layout.dialog_path_select
    }

    override fun getComponents(view: View?) {}
    override fun initData() {
        pathSelectFragment = PathSelectFragment()
    }

    override fun initView() {
        super.initView()
        mConfigData.fragmentManager = childFragmentManager
        Mtools.log("pathSelectFragment  show  start")
        FragmentTools.fragmentShowHide(
            mConfigData.fragmentManager,
            R.id.framelayout_dialog_show_body,
            pathSelectFragment,
            MConstants.TAG_ACTIVITY_FRAGMENT,
            true
        )
        Mtools.log("pathSelectFragment  show  end")
    }

    override fun onKey(dialog: DialogInterface, keyCode: Int, event: KeyEvent): Boolean {
        //会有两次点击先消费掉一次
        if (event.action != KeyEvent.ACTION_DOWN) {
            return true
        }
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            //此处捕获back操作，如果不希望所在的Activity监听到back键，需要返回true，消费掉。
            //让fragment先处理返回按钮事件
            if (pathSelectFragment != null && pathSelectFragment!!.onBackPressed()) {
                true
            } else {
                dismissAllowingStateLoss()
                true
            }
        } else {
            //这里注意当不是返回键时需将事件扩散，否则无法处理其他点击事件
            false
        }
    }
}