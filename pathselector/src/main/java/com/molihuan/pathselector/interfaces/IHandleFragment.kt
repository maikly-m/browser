package com.molihuan.pathselector.interfaces

import com.molihuan.pathselector.listener.CommonItemListener
import com.molihuan.pathselector.adapter.HandleListAdapter

interface IHandleFragment {
    val handleItemListeners: MutableList<CommonItemListener>?
    val handleListAdapter: HandleListAdapter?

    /**
     * 刷新 HandleList ui
     *
     * @return
     */
    fun refreshHandleList()
}