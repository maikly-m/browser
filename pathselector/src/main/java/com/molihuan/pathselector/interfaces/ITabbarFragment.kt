package com.molihuan.pathselector.interfaces

import com.molihuan.pathselector.adapter.TabbarListAdapter
import com.molihuan.pathselector.entity.TabbarFileBean

/**
 * @ClassName: AbstractTabbarFragment
 * @Author: molihuan
 * @Date: 2022/11/22/17:38
 * @Description: 面包屑Fragment
 */
interface ITabbarFragment {
    val tabbarListAdapter: TabbarListAdapter?
    val tabbarList: List<TabbarFileBean?>?
    fun updateTabbarList(): List<TabbarFileBean?>?
    fun updateTabbarList(path: String?): List<TabbarFileBean?>?

    /**
     * 刷新 TabbarList ui
     *
     * @return
     */
    fun refreshTabbarList()
}