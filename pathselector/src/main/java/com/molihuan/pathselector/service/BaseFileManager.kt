package com.molihuan.pathselector.service

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import com.molihuan.pathselector.service.impl.ConfigDataBuilderImpl
import com.molihuan.pathselector.entity.FileBean
import com.molihuan.pathselector.utils.FileTools
import com.molihuan.pathselector.utils.MConstants
import com.molihuan.pathselector.adapter.FileListAdapter
import com.molihuan.pathselector.adapter.TabbarListAdapter
import com.molihuan.pathselector.dao.SelectConfigData
import com.molihuan.pathselector.entity.TabbarFileBean
import java.io.File
import java.lang.IllegalArgumentException
import java.lang.StringBuilder
import java.util.*


abstract class BaseFileManager : IFileDataManager {
    protected var mConfigData: SelectConfigData = ConfigDataBuilderImpl.getInstance().selectConfigData
    @JvmField
    protected var mFileBeanController = mConfigData.fileBeanController
    override fun initFileList(currentPath: String, fileList: MutableList<FileBean>){
        //获取当前路径的上一级目录
        val parentPath = FileTools.getParentPath(currentPath)
        when (fileList.size) {
            0 -> {
                val fileBean = FileBean(parentPath, "...", MConstants.FILEBEAN_BACK_FLAG)
                fileBean.setFileIcoType(
                    mFileBeanController!!.getFileBeanImageResource(
                        true,
                        "This is back filebean item",
                        fileBean
                    )
                )
                fileList.add(fileBean)
            }
            else -> {
                fileList[0].let {
                    it.setPath(parentPath)
                    fileList.clear()
                    fileList.add(it)
                }
            }
        }
    }

    override fun clearFileListCache(fileList: MutableList<FileBean>) {
        for (i in fileList.indices.reversed()) {
            if (fileList[i].path == null) {
                fileList.removeAt(i)
            }
        }
    }

    override fun sortFileList(fileList: MutableList<FileBean>, sortType: Int, currentPath: String){
        Collections.sort(fileList, Comparator { o1, o2 ->
            //如果是空fileBean就换
            if (o1.path == null) {
                return@Comparator 1
            }
            if (o2.path == null) {
                return@Comparator -1
            }

            //如果是返回fileBean就不换
            if (o1.size == MConstants.FILEBEAN_BACK_FLAG) {
                return@Comparator -1
            }
            if (o2.size == MConstants.FILEBEAN_BACK_FLAG) {
                return@Comparator 1
            }

            //如果前面的是文件夹就不换,下面相反
            if (o1.isDir!! && !o2.isDir!!) {
                return@Comparator -1
            }
            if (!o1.isDir!! && o2.isDir!!) {
                return@Comparator 1
            }
            var diff = 0L
            when (sortType) {
                MConstants.SORT_NAME_ASC -> o1.name!!.compareTo(o2.name!!, ignoreCase = true) //根据名称字符串ASCLL码进行比较(忽略大小写)
                MConstants.SORT_NAME_DESC -> o2.name!!.compareTo(o1.name!!, ignoreCase = true)
                MConstants.SORT_TIME_ASC -> {
                    diff = o1.modifyTime!! - o2.modifyTime!!
                    if (diff > 0) 1 else if (diff == 0L) 0 else -1
                }
                MConstants.SORT_TIME_DESC -> {
                    diff = o2.modifyTime!! - o1.modifyTime!!
                    if (diff > 0) 1 else if (diff == 0L) 0 else -1
                }
                MConstants.SORT_SIZE_ASC -> {
                    diff = o1.size!! - o2.size!!
                    if (diff > 0) 1 else if (diff == 0L) 0 else -1
                }
                MConstants.SORT_SIZE_DESC -> {
                    diff = o2.size!! - o1.size!!
                    if (diff > 0) 1 else if (diff == 0L) 0 else -1
                }
                else -> 0
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun refreshFileTabbar(fileAdapter: FileListAdapter, tabbarAdapter: TabbarListAdapter, level: Int) {
        when (level) {
            TYPE_REFRESH_FILE -> {
                fileAdapter.notifyDataSetChanged()
            }
            TYPE_REFRESH_TABBAR -> {
                tabbarAdapter.notifyDataSetChanged()
            }
            TYPE_REFRESH_FILE_TABBAR -> {
                fileAdapter.notifyDataSetChanged()
                tabbarAdapter.notifyDataSetChanged()
            }
            else -> throw IllegalArgumentException("Parameter does not conform to a predefined value")
        }
    }

    override fun initTabbarList(initPath: String, tabbarList: MutableList<TabbarFileBean>) {
        when (tabbarList.size) {
            0 -> {}
            else -> {
                var i = tabbarList.size - 1
                while (i >= 0) {
                    tabbarList.removeAt(i)
                    i--
                }
            }
        }
    }

    override fun clearTabbarListCache(tabbarList: MutableList<TabbarFileBean>) {
        for (i in tabbarList.indices.reversed()) {
            if (tabbarList[i].path == null) {
                tabbarList.removeAt(i)
            }
        }
    }

    override fun updateTabbarList(
        initPath: String,
        currentPath: String,
        tabbarList: MutableList<TabbarFileBean>,
        tabbarAdapter: TabbarListAdapter
    ) {

        initTabbarList(currentPath, tabbarList)
        //通过/分割
        val parts = currentPath.split(File.separator).toTypedArray()
        if (parts.isEmpty()) {
            return
        }
        val builder = StringBuilder()
        /**组合成分级
         * parts[0] = (null)
         * parts[1] = /storage
         * parts[2] = /storage/emulated
         * parts[3] = /storage/emulated/0
         */
        for (i in 1 until parts.size) {
            parts[i] = builder.append(File.separator + parts[i]).toString()
        }
        var tabbarBean: TabbarFileBean
        for (i in 1 until parts.size) {
            if (false) {
                /**
                 * 如果还有缓存的FileBean就设置属性即可
                 * 0索引FileBean为返回按钮所以+1
                 */
                tabbarList!![i]
                    .setPath(parts[i])
                    .setName(FileTools.getFileName(parts[i]))
                    .setUseUri(false)
            } else {
                tabbarBean = TabbarFileBean()
                    .setPath(parts[i])
                    .setName(FileTools.getFileName(parts[i]))
                    .setUseUri(false)
                tabbarList.add(tabbarBean)
            }
        }
    }

    override fun initSelectedFileList(selectedList: MutableList<FileBean>){
        selectedList.clear()
    }

    override fun getSelectedFileList(
        allFileList: MutableList<FileBean>,
        selectedList: MutableList<FileBean>
    ) {
        initSelectedFileList(selectedList)
        Objects.requireNonNull<List<FileBean>>(allFileList, "allFileList is null")
        for (fileBean in allFileList) {
            if (fileBean.path != null && fileBean.boxChecked != null && fileBean.boxChecked!!) {
                selectedList.add(fileBean)
            }
        }
    }

    override fun returnDataToActivityResult(selectedFileList: MutableList<FileBean>, activity: Activity) {
        val selectedPath = ArrayList<String?>()
        for (bean in selectedFileList) {
            selectedPath.add(bean.path)
        }
        val result = Intent()
        result.putStringArrayListExtra(MConstants.CALLBACK_DATA_ARRAYLIST_STRING, selectedPath)
        activity.setResult(Activity.RESULT_OK, result) //设置返回原界面的结果
        activity.finish()
    }

    override fun setCheckBoxVisible(
        fileList: MutableList<FileBean>,
        fileAdapter: FileListAdapter,
        state: Boolean
    ) {
        var fileBean: FileBean
        for (i in fileList.indices) {
            fileBean = fileList[i]
            //判断是不是未使用的item
            if (fileBean.path == null) {
                break
            }
            //判断是不是返回item
            if (fileBean.size == MConstants.FILEBEAN_BACK_FLAG) {
                continue
            }
            fileBean.setBoxVisible(state)
            //显示和不显示时都设为不选中防止有缓存
            fileBean.setBoxChecked(false)
        }
    }

    override fun setBoxChecked(
        fileList: MutableList<FileBean>,
        fileAdapter: FileListAdapter,
        state: Boolean
    ) {
        var fileBean: FileBean
        for (i in fileList.indices) {
            fileBean = fileList[i]
            //判断是不是未使用的item
            if (fileBean.path == null) {
                break
            }
            //判断是不是返回item
            if (fileBean.size == MConstants.FILEBEAN_BACK_FLAG) {
                continue
            }
            fileBean.setBoxChecked(state)
        }
    }

    companion object {
        const val TYPE_REFRESH_FILE = 1
        const val TYPE_REFRESH_TABBAR = 2
        const val TYPE_REFRESH_FILE_TABBAR = 3
    }
}