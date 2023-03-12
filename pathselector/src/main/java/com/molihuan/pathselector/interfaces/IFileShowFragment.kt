package com.molihuan.pathselector.interfaces

import com.molihuan.pathselector.entity.FileBean
import com.molihuan.pathselector.adapter.FileListAdapter

/**
 * @ClassName: AbstractFileShowFragment
 * @Author: molihuan
 * @Date: 2022/11/22/17:39
 * @Description: 中间显示所以文件的Fragment抽象类
 */
interface IFileShowFragment {
    /**
     * 获取当前路径
     *
     * @return
     */
    val currentPath: String?

    /**
     * 获取选择的列表
     *
     * @return
     */
    val selectedFileList: MutableList<FileBean>
    val fileList: MutableList<FileBean>

    /**
     * 更新当前路径
     *
     * @return
     */
    fun updateFileList()

    /**
     * 根据路径更新列表
     *
     * @param currentPath
     * @return
     */
    fun updateFileList(currentPath: String)

    /**
     * 更新ui
     *
     * @return
     */
    fun refreshFileList()

    /**
     * 获取FileListAdapter
     *
     * @return
     */
    val fileListAdapter: FileListAdapter?

    /**
     * 全选或取消全选
     *
     * @param status
     */
    fun selectAllFile(status: Boolean)

    /**
     * 开启或关闭多选模式
     *
     * @param fileBean 可以为null
     * @param status
     */
    fun openCloseMultipleMode(fileBean: FileBean, status: Boolean)
    fun openCloseMultipleMode(status: Boolean)

    /**
     * 是否是多选模式
     *
     * @return
     */
    val isMultipleSelectionMode: Boolean
}