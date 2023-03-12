package com.molihuan.pathselector.fragment

import android.app.Dialog
import com.molihuan.pathselector.interfaces.IFileShowFragment
import com.molihuan.pathselector.interfaces.IHandleFragment
import com.molihuan.pathselector.interfaces.ITabbarFragment
import com.molihuan.pathselector.interfaces.ITitlebarFragment
import com.molihuan.pathselector.dao.SelectConfigData
import com.molihuan.pathselector.service.IFileDataManager
import com.molihuan.pathselector.controller.impl.DialogController

/**
 * @ClassName: BasePathSelectFragment
 * @Author: molihuan
 * @Date: 2022/11/23/19:20
 * @Description: 拥有了她就拥有了它就拥有了全世界(具体方法可以看她的实现类或者她的接口)
 */
abstract class BasePathSelectFragment : AbstractFragment(), IFileShowFragment, IHandleFragment, ITabbarFragment,
    ITitlebarFragment {
    abstract fun returnDataToActivityResult()
    abstract val selectConfigData: SelectConfigData?
    abstract val titlebarFragment: AbstractTitlebarFragment?
    abstract val tabbarFragment: AbstractTabbarFragment?
    abstract val fileShowFragment: AbstractFileShowFragment?
    abstract val handleFragment: AbstractHandleFragment?
    abstract val pathFileManager: IFileDataManager?
    abstract val uriFileManager: IFileDataManager?
    override fun dismiss() {
        if (mConfigData.buildController is DialogController) {
            mConfigData.buildController?.dialogFragment!!.dismiss()
        }
    }

    override fun getDialog(): Dialog? {
        return if (mConfigData.buildController is DialogController) {
            mConfigData.buildController?.dialogFragment!!.dialog
        } else null
    }

    /**
     * 显示或隐藏handleFragment
     *
     * @param isShow
     */
    abstract fun handleShowHide(isShow: Boolean)
}