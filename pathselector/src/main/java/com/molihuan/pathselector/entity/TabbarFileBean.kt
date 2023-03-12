package com.molihuan.pathselector.entity

import androidx.documentfile.provider.DocumentFile
import com.molihuan.pathselector.entity.TabbarFileBean
import java.io.Serializable

/**
 * @ClassName: TabbarFileBean
 * @Author: molihuan
 * @Date: 2022/11/22/21:51
 * @Description:
 */
class TabbarFileBean : Serializable {
    var path //文件路径(必须)
            : String? = null
        private set
    var name //名称
            : String? = null
        private set
    var flag: Long? = null
    var useUri //是否使用uri地址
            : Boolean? = null
        private set

    /**
     * 通过DocumentFile可以对Android/data目录进行操作
     * 具体怎么操作可以百度
     */
    var documentFile: DocumentFile? = null
        private set

    constructor() {}
    constructor(path: String?, name: String?) {
        this.path = path
        this.name = name
    }

    constructor(path: String?, name: String?, flag: Long?) {
        this.flag = flag
        this.path = path
        this.name = name
    }

    constructor(path: String?, name: String?, flag: Long?, useUri: Boolean?, documentFile: DocumentFile?) {
        this.flag = flag
        this.path = path
        this.name = name
        this.useUri = useUri
        this.documentFile = documentFile
    }

    fun clear() {
        flag = null
        path = null
        name = null
        useUri = null
        documentFile = null
    }

    fun setPath(path: String?): TabbarFileBean {
        this.path = path
        return this
    }

    fun setName(name: String?): TabbarFileBean {
        this.name = name
        return this
    }

    fun setUseUri(useUri: Boolean?): TabbarFileBean {
        this.useUri = useUri
        return this
    }

    fun setDocumentFile(documentFile: DocumentFile?): TabbarFileBean {
        this.documentFile = documentFile
        return this
    }

    override fun toString(): String {
        return "TabbarFileBean{" +
                "flag=" + flag +
                ", path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", useUri=" + useUri +
                ", documentFile=" + documentFile +
                '}'
    }
}