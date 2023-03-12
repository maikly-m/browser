package com.emoji.media.browser

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import com.emoji.media.browser.bean.DirInfo
import com.emoji.media.browser.bean.FileInfo
import com.emoji.media.browser.bean.PathInfo
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


fun parsePath(path: String?): MutableList<String>{
    //判断合法性
    path?.let {
        return mutableListOf<String>().apply {
            it.split("/").filter { s ->
                return@filter s != ""
            }.forEach { s ->
                add(s)
            }
        }
    }
    return mutableListOf()
}

fun isParentOrChildPath(path: String, filterPaths: List<String>): Boolean{
    filterPaths.forEach {
        if (it.startsWith(path) or path.startsWith(it)){
            return true
        }
    }
    return false
}

fun isParentPath(path: String?, filterPaths: List<String>):Boolean{
    filterPaths.forEach {
        if (path == null){
            return false
        }else{
            if (path.startsWith(it)){
                return true
            }
        }
    }
    return false
}

fun matchType(suffix: String, filters: List<String>):Boolean{
    filters.forEach {
        if (it == suffix){
            return true
        }
    }
    return false
}

const val splitFileCount = 15

fun findPathInfo(listPath: List<String>,
                 dirFilter: List<String>?,
                 fileFilter: List<String>?,
                 block :(DirInfo)->Unit ){
    var curPath = ""
    listPath.forEach { s ->
        curPath = "$curPath/$s"
    }
    File(curPath).let { cur_it ->
        if (cur_it.isDirectory) {
            val mutableListOf = mutableListOf<PathInfo>()
            //统计需要显示的目录和文件
            cur_it.listFiles()?.run {
                val d = DirInfo(
                    list = mutableListOf,
                    currentPath = cur_it.path.split("/").last(),
                    fullPath = cur_it.absolutePath.split("/")
                        .filter { s ->
                            return@filter s != ""
                        }.toMutableList(),
                    modifiedTime = millis2String(cur_it.lastModified(), getDefaultFormat()),
                )

                sortWith{ f1, f2 ->
                    if (f1.isDirectory && f2.isDirectory) {
                        f1.name.compareTo(f2.name)
                    } else if (f1.isDirectory and !f2.isDirectory){
                        -1
                    }else if (!f1.isDirectory and f2.isDirectory) {
                        1
                    }else{
                        f1.name.compareTo(f2.name)
                    }
                }

                var addSize = 0
                forEachIndexed {_, inner_file ->
                    if (inner_file.isDirectory) {
                        dirFilter?.let {
                            if (!isParentOrChildPath(inner_file.absolutePath, it)){
                                return@forEachIndexed
                            }
                        }
                        val list = inner_file.list()
                        mutableListOf.add(DirInfo(
                            childSize = list?.size ?: 0,
                            fullPath = inner_file.absolutePath.split("/").filter { s ->
                                return@filter s != ""
                            }.toMutableList(),
                            currentPath = inner_file.path.split("/").last(),
                            modifiedTime =  millis2String(inner_file.lastModified(), getDefaultFormat()))
                        )
                    } else {
                        dirFilter?.let {
                            fileFilter?.let { filter ->
                                if (!isParentPath(inner_file?.parent, it) or !matchType(inner_file.extension, filter)){
                                    return@forEachIndexed
                                }
                            }
                        }
                        mutableListOf.add(FileInfo(
                            byte2FitMemorySize(inner_file.length(), 2),
                            inner_file.extension,
                            inner_file.absolutePath.split("/")
                                .filter { s ->
                                    return@filter s != ""
                                }.toMutableList(),
                            inner_file.path.split("/").last(),
                            millis2String(inner_file.lastModified(), getDefaultFormat())
                        ))
                    }
                    if (addSize%splitFileCount == 0 && addSize != 0){
                        d.childSize = addSize
                        block(d)
                    }
                    addSize++
                }
                d.childSize = addSize
                block(d)
                return@let
            }
            DirInfo(
                list = mutableListOf,
                currentPath = cur_it.path.split("/").last(),
                fullPath = cur_it.absolutePath.split("/")
                    .filter { s ->
                        return@filter s != ""
                    }.toMutableList(),
                modifiedTime = millis2String(cur_it.lastModified(), getDefaultFormat())
            ).let {
                block(it)
            }
        }
    }
}

@SuppressLint("SimpleDateFormat")
fun getDefaultFormat(): SimpleDateFormat {
    return SimpleDateFormat("yyyy-MM-dd HH:mm")
}
fun millis2String(millis: Long, format: DateFormat): String {
    return format.format(Date(millis))
}

fun byte2FitMemorySize(byteSize: Long, precision: Int): String? {
    require(precision >= 0) { "precision shouldn't be less than zero!" }
    return if (byteSize < 0) {
        throw IllegalArgumentException("byteSize shouldn't be less than zero!")
    } else if (byteSize < Constants.KB) {
        String.format("%." + precision + "fB", byteSize.toDouble())
    } else if (byteSize < Constants.MB) {
        java.lang.String.format("%." + precision + "fKB", byteSize.toDouble() / Constants.KB)
    } else if (byteSize < Constants.GB) {
        java.lang.String.format("%." + precision + "fMB", byteSize.toDouble() / Constants.MB)
    } else {
        java.lang.String.format("%." + precision + "fGB", byteSize.toDouble() / Constants.GB)
    }
}

/**
 * dp转dp
 *
 * @param dp dp
 * @return 返回转换后的dp的值
 */
fun dp2px(dp: Float): Int {
    val scale = Resources.getSystem().displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}

fun dip2pxF(dpValue: Float): Float {
    val scale = Resources.getSystem().displayMetrics.density
    return dpValue * scale
}

/**
 * px转dp
 *
 * @param px px的值
 * @return 返回转换后的dip的值
 */
fun px2dp(px: Float): Int {
    val scale = Resources.getSystem().displayMetrics.density
    return (px / scale + 0.5f).toInt()
}

/**
 * sp转px
 *
 * @param sp sp的值
 * @return 返回转换为像素后的值
 */
fun sp2px(sp: Float): Int {
    val scale = Resources.getSystem().displayMetrics.scaledDensity
    return (sp * scale).toInt()
}

/**
 * @param spValue
 * @param fontScale (DisplayMetrics类中的scaledDensity属性)
 * @return
 */
fun sp2pix(spValue: Float, fontScale: Float): Int {
    return (spValue * fontScale + 0.5f).toInt()
}

/**
 * px转sp
 *
 * @param px px的值
 * @return 返回转换后的px的值
 */
fun px2sp(px: Float): Int {
    val scale = Resources.getSystem().displayMetrics.scaledDensity
    return (px / scale + 0.5f).toInt()
}

/**
 * 通过屏幕长宽来判断是否是竖屏
 *
 * @param context 上下文
 * @return 是否是竖屏
 */
fun isVerticalScreen(context: Context): Boolean {
    val configuration = context.resources.configuration
    return configuration.orientation == Configuration.ORIENTATION_PORTRAIT
}