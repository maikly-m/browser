package com.molihuan.pathselector.controller.impl

import androidx.annotation.CallSuper
import com.molihuan.pathselector.R
import com.molihuan.pathselector.controller.AbstractFileBeanController
import com.molihuan.pathselector.entity.FileBean


class FileBeanControllerImpl : AbstractFileBeanController() {
    @CallSuper
    override fun getFileBeanImageResource(isDir: Boolean, extension: String?, fileBean: FileBean?): Int {
        val resourceId: Int = when (extension) {
            "apk" -> R.mipmap.apk
            "avi" -> R.mipmap.avi
            "doc", "docx" -> R.mipmap.doc
            "exe" -> R.mipmap.exe
            "flv" -> R.mipmap.flv
            "gif" -> R.mipmap.gif
            "jpg", "jpeg", "png" -> R.mipmap.png
            "mp3" -> R.mipmap.mp3
            "mp4", "f4v" -> R.mipmap.movie
            "pdf" -> R.mipmap.pdf
            "ppt", "pptx" -> R.mipmap.ppt
            "wav" -> R.mipmap.wav
            "xls", "xlsx" -> R.mipmap.xls
            "zip" -> R.mipmap.zip
            "ext" -> if (isDir) {
                R.mipmap.folder
            } else {
                R.mipmap.documents
            }
            else -> if (isDir) {
                R.mipmap.folder
            } else {
                R.mipmap.documents
            }
        }
        return resourceId
    }
}