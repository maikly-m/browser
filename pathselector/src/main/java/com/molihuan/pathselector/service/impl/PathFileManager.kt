package com.molihuan.pathselector.service.impl

import androidx.fragment.app.Fragment
import com.blankj.molihuan.utilcode.util.FileUtils
import com.molihuan.pathselector.entity.FileBean
import com.molihuan.pathselector.adapter.FileListAdapter
import com.molihuan.pathselector.service.BaseFileManager
import com.molihuan.pathselector.utils.FileTools


class PathFileManager : BaseFileManager() {
    override fun updateFileList(
        fragment: Fragment,
        initPath: String,
        currentPath: String,
        fileList: MutableList<FileBean>,
        fileAdapter: FileListAdapter,
        fileTypeList: MutableList<String>
    ) {
        initFileList(currentPath, fileList)
        //列表中存在但未初始化的FileBean个数，即列表中FileBean所有字段都为null的个数
        val cacheFileSize = fileList.size - 1
        val file = FileUtils.getFileByPath(currentPath) ?: return
        val subFiles = file.listFiles()
        var extension: String
        var addNumber = 0 //添加的数量
        var fileBean: FileBean
        if (subFiles != null) {
            for (i in subFiles.indices) {
                //获取后缀
                extension = FileUtils.getFileExtension(subFiles[i])
                //fileTypeList为null或者数量为0说明不限制类型.添加文件后缀符合要求的、添加文件夹、没有要求就都添加
                if (fileTypeList == null || fileTypeList.size == 0 || fileTypeList.contains(extension) || subFiles[i].isDirectory) {
                    if (addNumber < cacheFileSize) {
                        /**
                         * 如果还有缓存的FileBean就设置属性即可
                         * 0索引FileBean为返回按钮所以+1
                         */
                        fileBean = fileList[addNumber + 1]
                        fileBean.setPath(subFiles[i].absolutePath)
                            .setName(subFiles[i].name)
                            .setDir(subFiles[i].isDirectory)
                            .setFileExtension(extension)
                            .setChildrenFileNumber(FileTools.getChildrenNumber(subFiles[i])[0])
                            .setChildrenDirNumber(FileTools.getChildrenNumber(subFiles[i])[1])
                            .setBoxVisible(false)
                            .setBoxChecked(false)
                            .setModifyTime(subFiles[i].lastModified())
                            .setSize(subFiles[i].length())
                            .setSizeString(FileTools.computeFileSize(subFiles[i]))
                            .setUseUri(false) //需要放在最后
                            .setFileIcoType(
                                mFileBeanController!!.getFileBeanImageResource(
                                    subFiles[i].isDirectory,
                                    extension,
                                    fileBean
                                )
                            )
                    } else {
                        //如果不够就new
                        fileBean = FileBean()
                        fileBean.setPath(subFiles[i].absolutePath)
                            .setName(subFiles[i].name)
                            .setDir(subFiles[i].isDirectory)
                            .setFileExtension(extension)
                            .setChildrenFileNumber(FileTools.getChildrenNumber(subFiles[i])[0])
                            .setChildrenDirNumber(FileTools.getChildrenNumber(subFiles[i])[1])
                            .setBoxVisible(false)
                            .setBoxChecked(false)
                            .setModifyTime(subFiles[i].lastModified())
                            .setSize(subFiles[i].length())
                            .setSizeString(FileTools.computeFileSize(subFiles[i]))
                            .setUseUri(false)
                            .setFileIcoType(
                                mFileBeanController!!.getFileBeanImageResource(
                                    subFiles[i].isDirectory,
                                    extension,
                                    fileBean
                                )
                            )
                        fileList.add(fileBean)
                    }
                    addNumber++ //添加数量增加
                }
            }
        }
    }
}