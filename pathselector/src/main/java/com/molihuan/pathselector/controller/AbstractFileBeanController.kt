package com.molihuan.pathselector.controller

import com.molihuan.pathselector.entity.FileBean

abstract class AbstractFileBeanController {
    abstract fun getFileBeanImageResource(isDir: Boolean, extension: String?, fileBean: FileBean?): Int
}