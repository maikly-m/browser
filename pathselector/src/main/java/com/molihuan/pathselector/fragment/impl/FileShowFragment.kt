package com.molihuan.pathselector.fragment.impl

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.molihuan.pathselector.fragment.AbstractFileShowFragment
import com.molihuan.pathselector.entity.FileBean
import com.molihuan.pathselector.adapter.FileListAdapter
import com.molihuan.pathselector.service.IFileDataManager
import com.molihuan.pathselector.listener.FileItemListener
import com.molihuan.pathselector.utils.CommonTools
import com.molihuan.pathselector.utils.FileTools
import com.xuexiang.xtask.XTask
import com.xuexiang.xtask.core.step.impl.TaskCommand
import kotlin.Throws
import com.xuexiang.xtask.core.step.impl.TaskChainCallbackAdapter
import com.xuexiang.xtask.core.ITaskChainEngine
import com.xuexiang.xtask.core.param.ITaskResult
import com.molihuan.pathselector.service.impl.PathFileManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.chad.library.adapter.base.listener.OnItemLongClickListener
import com.molihuan.pathselector.R
import com.molihuan.pathselector.databinding.FragmentFileShowBinding
import com.molihuan.pathselector.service.BaseFileManager.Companion.TYPE_REFRESH_FILE
import com.molihuan.pathselector.service.BaseFileManager.Companion.TYPE_REFRESH_FILE_TABBAR
import com.molihuan.pathselector.utils.Mtools
import com.molihuan.pathselector.utils.MConstants
import java.lang.Exception

class FileShowFragment : AbstractFileShowFragment(), OnItemClickListener, OnItemLongClickListener {
    private lateinit var binding: FragmentFileShowBinding
    //最初路径
    var initPath: String? = null

    //当前路径
    override var currentPath: String? = null

    //List和Adapter
    override var selectedFileList: MutableList<FileBean> = mutableListOf()
        get() {
            pathFileManager!!.getSelectedFileList(fileList, field)
            return field
        }

    override var fileList: MutableList<FileBean> = mutableListOf()
        private set
    override var fileListAdapter: FileListAdapter? = null
        private set

    //单选
    private var radio: Boolean? = null

    //排序类型
    private var sortType: Int? = null

    //文件显示类型
    private var showFileTypes: List<String>? = null

    //选择类型
    private var selectFileTypes: List<String>? = null

    //路径管理者
    private var pathFileManager: IFileDataManager? = null

    //uri管理者
    private var uriFileManager: IFileDataManager? = null

    //当前是否为多选模式
    override var isMultipleSelectionMode = false
        private set

    //多选数量
    private var selectedNumber = 0

    //fileItem监听
    private var fileItemListener: FileItemListener? = null

    override fun setFragmentView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentFileShowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initData() {
        super.initData()

        //获取路径管理者
        pathFileManager = psf!!.pathFileManager
        //获取uri管理者
        uriFileManager = psf!!.uriFileManager
        //获取初始路径并设置当前路径
        initPath = mConfigData.rootPath
        currentPath = initPath
        //获取配置数据
        radio = mConfigData.radio
        sortType = mConfigData.sortType
        showFileTypes = CommonTools.asStringList(mConfigData.showFileTypes)
        selectFileTypes = CommonTools.asStringList(mConfigData.selectFileTypes)
        //获取监听器
        fileItemListener = mConfigData.fileItemListener

        //获取文件列表数据
        initFileList()
    }

    override fun initView() {
        //第一次则设置Adapter和监听
        if (fileListAdapter == null) {
            binding.recvFileShow.layoutManager = LinearLayoutManager(
                activity,
                LinearLayoutManager.VERTICAL,
                false
            ) //设置布局管理者
            fileListAdapter = FileListAdapter(R.layout.item_file, fileList as MutableList<FileBean>?) //适配器添加数据
            binding.recvFileShow.adapter = fileListAdapter //RecyclerView设置适配器
            fileListAdapter!!.setOnItemClickListener(this)
            fileListAdapter!!.setOnItemLongClickListener(this)
        }
        //更新
        updateFileList()
    }

    override fun setListeners() {}
    private fun initFileList() {
        currentPath?.let {
            if (FileTools.needUseUri(it)) {
                uriFileManager!!.initFileList(it, fileList)
            } else {
                pathFileManager!!.initFileList(it, fileList)
            }
        }

    }

    override fun updateFileList() {
        currentPath?.let {
            updateFileList(it)
        }
    }

    override fun updateFileList(path: String) {

        //更新当前路径
        currentPath = path

        //开始异步获取文件列表数据
        XTask.getTaskChain()
            .addTask(XTask.getTask(object : TaskCommand() {
                @Throws(Exception::class)
                override fun run() {
                    //是否需要使用uri
                    if (FileTools.needUseUri(path)) {
                        uriFileManager!!.updateFileList(psf, initPath, path, fileList, fileListAdapter, showFileTypes)
                        //排序
                        uriFileManager!!.sortFileList(fileList, sortType!!, currentPath)
                    } else {
                        pathFileManager!!.updateFileList(psf, initPath, path, fileList, fileListAdapter, showFileTypes)
                        //排序
                        pathFileManager!!.sortFileList(fileList, sortType!!, currentPath)
                    }
                }
            }))
            .setTaskChainCallback(object : TaskChainCallbackAdapter() {
                override fun onTaskChainCompleted(engine: ITaskChainEngine, result: ITaskResult) {

                    //更新ui
                    if (FileTools.needUseUri(path)) {
                        //刷新
                        uriFileManager!!.refreshFileTabbar(fileListAdapter, null, PathFileManager.TYPE_REFRESH_FILE)
                    } else {
                        //刷新
                        pathFileManager!!.refreshFileTabbar(fileListAdapter, null, PathFileManager.TYPE_REFRESH_FILE)
                    }
                }
            })
            .start()
    }

    override fun refreshFileList() {
        pathFileManager!!.refreshFileTabbar(fileListAdapter, psf!!.tabbarListAdapter, PathFileManager.TYPE_REFRESH_FILE_TABBAR)
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, v: View, position: Int) {
        if (adapter is FileListAdapter) {
            val item = fileList!![position]
            //如果当前已经是多选模式
            if (isMultipleSelectionMode && !radio!!) {
                //多选模式下不能点击返回item
                if (position == 0) {
                    return
                }

                //选择类型正确
                if (FileTools.selectTypeCompliance(item!!.fileExtension, selectFileTypes)) {

                    //如果只选择一个
                    if (mConfigData.maxCount == 1) {
                        pathFileManager!!.setBoxChecked(fileList, null, false)
                        item.setBoxChecked(true)
                    } else {

                        //如果已经勾选了
                        if (item.boxChecked!!) {
                            item.setBoxChecked(false)
                            selectedNumber--
                        } else if (selectedNumber + 1 <= mConfigData.maxCount!! || mConfigData.maxCount == -1) {
                            //没有勾选且没有超过最大数量、或最大数量是-1则不限制
                            item.setBoxChecked(true)
                            selectedNumber++
                        } else {
                            //超过选择的最大数量
                            Mtools.toast(getString(R.string.tip_filebeanitem_select_limit_exceeded))
                        }
                    }
                } else {
                    Mtools.toast(getString(R.string.tip_filebeanitem_select_error_type))
                }
                pathFileManager!!.refreshFileTabbar(fileListAdapter, null, PathFileManager.TYPE_REFRESH_FILE)
            } else {

                //如果是返回FileBean
                if (position == 0) {
                    var path = item!!.path
                    //如果当前路径比"/storage/emulated/0"还短则设置currentPath为"/storage/emulated/0"
                    if (path!!.length <= MConstants.DEFAULT_ROOTPATH.length && MConstants.DEFAULT_ROOTPATH != path) {
                        Mtools.toast(
                            String.format(
                                getString(R.string.tips_path_jump_error_exceeds_default_path),
                                path,
                                MConstants.DEFAULT_ROOTPATH
                            )
                        )
                        path = MConstants.DEFAULT_ROOTPATH
                    }
                    updateFileList(path) //更新当前路径
                    //刷新面包屑
                    psf!!.updateTabbarList(path)
                    return
                }

                //如果是文件夹
                if (item!!.isDir!!) {
                    updateFileList(item.path) //更新当前路径
                    //刷新面包屑
                    psf!!.updateTabbarList()
                } else {
                    //选择类型正确
                    if (FileTools.selectTypeCompliance(item.fileExtension, selectFileTypes)) {

                        //如果设置了fileItem监听
                        if (fileItemListener != null) {
                            val handled = fileItemListener!!.onClick(v, item, currentPath, psf)
                            //已经处理完了就不需要再处理了
                            if (handled) {
                                return
                            }
                        }
                        pathFileManager!!.setBoxChecked(fileList, null, false)
                        item.setBoxChecked(true)
                    }
                }
            }
        }
    }

    override fun onItemLongClick(adapter: BaseQuickAdapter<*, *>, v: View, position: Int): Boolean {
        if (adapter is FileListAdapter) {
            val fileBean = fileList!![position]
            //根据配置判断是否可以是使用多选,返回item不能长按
            if (!radio!! && position != 0) {

                //如果设置了fileItem监听
                if (fileItemListener != null) {
                    val handled = fileItemListener!!.onLongClick(v, fileBean, currentPath, psf)
                    //已经处理完了就不需要再处理了
                    if (handled) {
                        return true
                    }
                }
                openCloseMultipleMode(fileBean, !isMultipleSelectionMode)
                return true
            }
        }
        return false
    }

    override fun selectAllFile(status: Boolean) {
        //只有允许多选并且当前是多选的情况下才可以
        if (!radio!! && isMultipleSelectionMode) {
            pathFileManager!!.setBoxChecked(fileList, null, status)
            pathFileManager!!.refreshFileTabbar(fileListAdapter, null, PathFileManager.TYPE_REFRESH_FILE)
        }
    }

    override fun openCloseMultipleMode(fileBean: FileBean?, status: Boolean) {
        //长按进行多选模式切换
        isMultipleSelectionMode = status
        //显示隐藏checkbox
        pathFileManager!!.setCheckBoxVisible(fileList, null, isMultipleSelectionMode)
        psf!!.handleShowHide(isMultipleSelectionMode)

        //如果是多选模式则勾选当前长按的选项
        if (isMultipleSelectionMode) {
            //选择类型正确
            if (fileBean != null && FileTools.selectTypeCompliance(fileBean.fileExtension, selectFileTypes)) {
                fileBean.setBoxChecked(true)
                selectedNumber++
            }
        }
        //刷新
        pathFileManager!!.refreshFileTabbar(fileListAdapter, null, PathFileManager.TYPE_REFRESH_FILE)
    }

    override fun openCloseMultipleMode(status: Boolean) {
        openCloseMultipleMode(null, status)
    }

    override fun onBackPressed(): Boolean {
        //如果当前是多选模式则先退出多选模式
        if (isMultipleSelectionMode) {
            openCloseMultipleMode(false)
            return true
        }
        val path = fileList!![0]!!.path
        //路径超过了最初的路径则直接返回
        return if (!path!!.startsWith(initPath!!)) {
            false
        } else {
            //更新当前路径
            currentPath = path
            updateFileList(currentPath)
            //刷新面包屑
            psf!!.updateTabbarList()
            true
        }
    }
}