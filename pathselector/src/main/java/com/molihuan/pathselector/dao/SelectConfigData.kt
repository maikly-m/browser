package com.molihuan.pathselector.dao

import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.fragment.app.FragmentManager
import com.molihuan.pathselector.controller.AbstractBuildController
import com.molihuan.pathselector.fragment.AbstractTitlebarFragment
import com.molihuan.pathselector.entity.FontBean
import com.molihuan.pathselector.listener.CommonItemListener
import com.molihuan.pathselector.fragment.AbstractTabbarFragment
import com.molihuan.pathselector.fragment.AbstractFileShowFragment
import com.molihuan.pathselector.listener.FileItemListener
import com.molihuan.pathselector.controller.AbstractFileBeanController
import com.molihuan.pathselector.fragment.AbstractHandleFragment
import com.molihuan.pathselector.utils.Mtools
import com.molihuan.pathselector.utils.MConstants
import com.blankj.molihuan.utilcode.util.ScreenUtils
import com.molihuan.pathselector.R
import kotlin.Throws
import com.molihuan.pathselector.fragment.impl.FileShowFragment
import com.molihuan.pathselector.fragment.impl.TitlebarFragment
import com.molihuan.pathselector.fragment.impl.TabbarFragment
import com.molihuan.pathselector.fragment.impl.HandleFragment
import com.molihuan.pathselector.controller.impl.FileBeanControllerImpl

class SelectConfigData {
    var context //上下文
            : Context? = null

    @JvmField
    var buildType //构建类型
            : Int? = null

    @JvmField
    var buildController //构建控制
            : AbstractBuildController? = null

    @JvmField
    var requestCode //请求码
            : Int? = null

    @JvmField
    var frameLayoutId //添加fragment地方
            : Int? = null

    @JvmField
    var sortType //排序类型
            : Int? = null

    @JvmField
    var radio //是否是单选非单选则是多选，多选必须设为false
            : Boolean? = null

    @JvmField
    var maxCount //多选的个数
            : Int? = null

    @JvmField
    var rootPath //默认目录
            : String? = null

    @JvmField
    var fragmentManager //fragment管理者
            : FragmentManager? = null

    @JvmField
    var showFileTypes //显示文件类型
            : Array<String>? = null

    @JvmField
    var selectFileTypes //选择文件类型
            : Array<String>? = null

    @JvmField
    var showSelectStorageBtn //是否显示选择内部存储按钮
            : Boolean? = null

    /******************   PathSelectDialog    */
    @JvmField
    var pathSelectDialogWidth: Int? = null

    @JvmField
    var pathSelectDialogHeight: Int? = null

    /******************   TitlebarFragment    */
    @JvmField
    var titlebarFragment //标题Fragment
            : AbstractTitlebarFragment? = null

    @JvmField
    var showTitlebarFragment //是否显示标题Fragment
            : Boolean? = null

    @JvmField
    var titlebarMainTitle //主标题字体
            : FontBean? = null

    @JvmField
    var titlebarSubtitleTitle //副标题字体
            : FontBean? = null

    @JvmField
    var titlebarBG //标题背景色
            : Int? = null

    //TODO 将数组转成 list处理
    @JvmField
    var morePopupItemListeners //更多popup Item监听器
            : Array<CommonItemListener>? = null

    /******************   TabbarFragment    */
    @JvmField
    var tabbarFragment //面包屑Fragment
            : AbstractTabbarFragment? = null

    @JvmField
    var showTabbarFragment //是否显示面包屑Fragment
            : Boolean? = null

    /******************   FileShowFragment    */
    @JvmField
    var fileShowFragment //文件显示列表Fragment
            : AbstractFileShowFragment? = null

    @JvmField
    var fileItemListener //文件item监听器
            : FileItemListener? = null

    @JvmField
    var fileBeanController //FileBean item控制器
            : AbstractFileBeanController? = null

    /******************   HandleFragment    */
    @JvmField
    var handleFragment //最下方按钮Fragment
            : AbstractHandleFragment? = null

    @JvmField
    var showHandleFragment //是否显示最下方按钮Fragment
            : Boolean? = null

    @JvmField
    var alwaysShowHandleFragment //总是显示HandleFragment
            : Boolean? = null

    @JvmField
    var handleItemListeners //最下方按钮 Item监听器
            : Array<CommonItemListener>? = null
    /**
     * 初始化默认配置
     */
    fun initDefaultConfig(context: Context) {
        Mtools.log("默认配置SelectConfigData  init  start")
        this.context = context //必须要设置
        buildType = null //必须要设置
        //buildController = null;//buildType设置即可，会自动覆盖
        requestCode = null //非必须(activity模式必须)
        frameLayoutId = null //非必须(fragment模式必须)
        sortType = MConstants.SORT_NAME_ASC
        radio = false //默认多选
        maxCount = -1 //不限制
        rootPath = MConstants.DEFAULT_ROOTPATH
        fragmentManager = null
        showFileTypes = null //null表示所有类型
        selectFileTypes = null //null表示所有类型
        showSelectStorageBtn = true
        pathSelectDialogWidth = ScreenUtils.getScreenWidth() * 80 / 100
        pathSelectDialogHeight = ScreenUtils.getScreenHeight() * 80 / 100
        showTitlebarFragment = true
        titlebarMainTitle = null //没有标题
        titlebarSubtitleTitle = null //没有副标题
        titlebarBG = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.getColor(R.color.orange) //橙色
        } else {
            Color.rgb(255, 165, 0)
        }
        morePopupItemListeners = null //空即没有
        showTabbarFragment = true
        fileItemListener = null //空即没有
        fileBeanController = null
        showHandleFragment = true
        alwaysShowHandleFragment = false
        handleItemListeners = null //空即没有
        titlebarFragment = null
        tabbarFragment = null
        handleFragment = null
        Mtools.log("默认配置SelectConfigData  init  end")
    }

    /**
     * TODO 用到了反射
     * 初始化各种Fragment
     * 自定义的fragment必须通过反射获取
     * TODO 可以优化    思路:判断一个对象是一个类的子类的实例
     *
     *
     * 必须通过反射获取新的实例，可以处理用户设置的视图和已经存在的视图
     *
     *
     * class A {
     * }
     * class B extends A {
     * }
     * class C extends A {
     * }
     * class D extends B {
     * }
     *
     *
     * A a = new A();
     * B b = new B();
     * C c = new C();
     * D d = new D();
     *
     *
     * System.out.println(b.getClass().isAssignableFrom(B.class));//true
     * System.out.println(c.getClass().isAssignableFrom(B.class));//false
     * System.out.println(d.getClass().isAssignableFrom(B.class));//false
     */
    @Throws(IllegalAccessException::class, InstantiationException::class)
    fun initAllFragment() {
        Mtools.log("各种Fragment  init  start")
        //当对象是空或者对象一定是FileShowFragment类的实例（不是其子类实例）时可以通过new来实例化否则只能通过反射来获取(增加性能)
        fileShowFragment =
            if (fileShowFragment == null || fileShowFragment!!.javaClass.isAssignableFrom(FileShowFragment::class.java)) {
                //一般来说FileShowFragment必须用默认的， 特殊情况可以特殊处理
                FileShowFragment() //必须先初始化
            } else {
                //通过反射来获取自定义Fragment实例
                fileShowFragment!!.javaClass.newInstance()
            }

        //使用自定义视图  或者  不需要显示则不创建
        if (showTitlebarFragment!!) {
            titlebarFragment =
                if (titlebarFragment == null || titlebarFragment!!.javaClass.isAssignableFrom(TitlebarFragment::class.java)) {
                    //使用默认的titlebar
                    TitlebarFragment()
                } else {
                    titlebarFragment!!.javaClass.newInstance()
                }
        }
        if (showTabbarFragment!!) {
            tabbarFragment =
                if (tabbarFragment == null || tabbarFragment!!.javaClass.isAssignableFrom(TabbarFragment::class.java)) {
                    TabbarFragment()
                } else {
                    tabbarFragment!!.javaClass.newInstance()
                }
        }
        if (showHandleFragment!!) {
            handleFragment =
                if (handleFragment == null || handleFragment!!.javaClass.isAssignableFrom(HandleFragment::class.java)) {
                    HandleFragment()
                } else {
                    handleFragment!!.javaClass.newInstance()
                }
        }
        Mtools.log("各种Fragment  init  end")
    }

    /**
     * 初始化控制器
     */
    @Throws(IllegalAccessException::class, InstantiationException::class)
    fun initController() {
        Mtools.log("控制器Controller  init  start")
        if (fileBeanController == null) {
            //没有设置就使用默认的fileBean控制器
            fileBeanController = FileBeanControllerImpl()
        }
        Mtools.log("控制器Controller  init  end")
    }
}