package com.molihuan.pathselector.controller

import com.molihuan.pathselector.dao.SelectConfigData
import com.molihuan.pathselector.service.impl.ConfigDataBuilderImpl
import com.molihuan.pathselector.fragment.impl.PathSelectFragment
import com.molihuan.pathselector.dialog.AbstractFragmentDialog

//TODO 还需要进行抽象
abstract class AbstractBuildController {
    @JvmField
    protected var mConfigData: SelectConfigData = ConfigDataBuilderImpl.getInstance().selectConfigData
    abstract fun show(): PathSelectFragment?
    abstract val pathSelectFragment: PathSelectFragment?
    abstract val dialogFragment: AbstractFragmentDialog?
}