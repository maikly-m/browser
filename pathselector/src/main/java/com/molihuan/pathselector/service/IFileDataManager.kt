package com.molihuan.pathselector.service

import android.app.Activity
import androidx.fragment.app.Fragment
import com.molihuan.pathselector.entity.FileBean
import com.molihuan.pathselector.adapter.FileListAdapter
import com.molihuan.pathselector.entity.TabbarFileBean
import com.molihuan.pathselector.adapter.TabbarListAdapter

interface IFileDataManager {
    /**
     * @param currentPath
     * @param fileList
     * @return
     */
    fun initFileList(currentPath: String, fileList: MutableList<FileBean>)

    /**
     * 清理掉没有使用的FileList item
     *
     * @param fileList
     * @return
     */
    fun clearFileListCache(fileList: MutableList<FileBean>)

    /**
     * 获取或更新文件列表
     *
     * @param initPath
     * @param currentPath
     * @param fileList
     * @param fileAdapter
     * @param fileTypes
     * @return
     */
    fun updateFileList(
        fragment: Fragment,
        initPath: String,
        currentPath: String,
        fileList: MutableList<FileBean>,
        fileAdapter: FileListAdapter,
        fileTypes: MutableList<String>
    )

    /**
     * 排序文件列表
     *
     * @param fileList
     * @param sortType
     * @return
     */
    fun sortFileList(fileList: MutableList<FileBean>, sortType: Int, currentPath: String)
    fun initTabbarList(initPath: String, tabbarList: MutableList<TabbarFileBean>)

    /**
     * 清理掉缓存TabbarList item
     *
     * @param tabbarList
     * @return
     */
    fun clearTabbarListCache(tabbarList: MutableList<TabbarFileBean>)

    /**
     * 获取或更新tabbar
     * 初始化or添加：
     * 以最初的路径为基础，以/为分割，将当前路径分割，
     * 如：最初路径为：/storage/emulated/0当当前路径为/storage/emulated/0/Tencent/ams时应该
     * 分割成：1.（/storage/emulated/0）2.（/storage/emulated/0/Tencent）3.（/storage/emulated/0/Tencent/ams）
     *
     * @param currentPath   当前路径
     * @param tabbarList
     * @param tabbarAdapter
     * @return
     */
    fun updateTabbarList(
        initPath: String,
        currentPath: String,
        tabbarList: MutableList<TabbarFileBean>,
        tabbarAdapter: TabbarListAdapter
    )

    fun initSelectedFileList(selectedList: MutableList<FileBean>)

    /**
     * 获取选择的列表
     *
     * @param allFileList
     * @param selectedList
     * @return
     */
    fun getSelectedFileList(allFileList: MutableList<FileBean>, selectedList: MutableList<FileBean>)

    /**
     * 返回选择的文件数据列表给Activity的onActivityResult()
     *
     * @return
     */
    fun returnDataToActivityResult(selectedFileList: MutableList<FileBean>, activity: Activity)

    /**
     * 设置CheckBox显示、隐藏
     *
     * @param fileList
     * @param fileAdapter
     * @param state
     * @return
     */
    fun setCheckBoxVisible(fileList: MutableList<FileBean>, fileAdapter: FileListAdapter, state: Boolean)

    /**
     * 设置是否选中
     *
     * @param fileList
     * @param fileAdapter
     * @param state
     * @return
     */
    fun setBoxChecked(fileList: MutableList<FileBean>, fileAdapter: FileListAdapter, state: Boolean)

    /**
     * 刷新File、Tabbar
     *
     * @param level 0 1 2 3
     * @return
     */
    fun refreshFileTabbar(fileAdapter: FileListAdapter, tabbarAdapter: TabbarListAdapter, level: Int)
}