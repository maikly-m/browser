package com.molihuan.pathselector.entity

/**
 * @ClassName: StorageBean
 * @Author: molihuan
 * @Date: 2022/12/11/16:26
 * @Description:
 */
class StorageBean {
    var rootPath: String? = null
    var selected: Boolean? = null

    constructor() {}
    constructor(rootPath: String?, selected: Boolean?) {
        this.rootPath = rootPath
        this.selected = selected
    }

    override fun toString(): String {
        return "StorageBean{" +
                "rootPath='" + rootPath + '\'' +
                ", selected=" + selected +
                '}'
    }
}