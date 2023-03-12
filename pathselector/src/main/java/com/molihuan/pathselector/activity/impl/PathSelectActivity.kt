package com.molihuan.pathselector.activity.impl

import android.view.View
import com.molihuan.pathselector.R
import com.molihuan.pathselector.activity.AbstractActivity
import com.molihuan.pathselector.databinding.ActivityPathSelectBinding
import com.molihuan.pathselector.fragment.impl.PathSelectFragment
import com.molihuan.pathselector.utils.FragmentTools
import com.molihuan.pathselector.utils.MConstants

class PathSelectActivity : AbstractActivity() {
    private lateinit var binding: ActivityPathSelectBinding
    private var pathSelectFragment: PathSelectFragment? = null
    override fun setContentView(): View {
        binding = ActivityPathSelectBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initData() {}
    override fun initView() {
        super.initView()
        pathSelectFragment = PathSelectFragment()
        mConfigData.fragmentManager = supportFragmentManager
        ////加载 PathSelectFragment
        FragmentTools.fragmentShowHide(
            mConfigData.fragmentManager,
            R.id.framelayout_show_body,
            pathSelectFragment,
            MConstants.TAG_ACTIVITY_FRAGMENT,
            true
        )
    }

    override fun onBackPressed() {
        //让fragment先处理返回按钮事件
        if (pathSelectFragment != null && pathSelectFragment!!.onBackPressed()) {
            return
        }
        super.onBackPressed()
    }

    override fun setListeners() {}
}