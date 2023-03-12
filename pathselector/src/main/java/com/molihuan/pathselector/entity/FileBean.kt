package com.molihuan.pathselector.entity

import androidx.documentfile.provider.DocumentFile
import com.molihuan.pathselector.entity.FileBean
import java.io.Serializable

class FileBean : Serializable {
    var path //文件路径(必须)
            : String? = null
        private set
    var name //名称
            : String? = null
        private set
    var isDir //是否是文件夹
            : Boolean? = null
        private set
    var fileExtension //后缀
            : String? = null
        private set
    var fileIcoType //文件图片类型
            : Int? = null
        private set
    var childrenFileNumber //子文件数量
            : Int? = null
        private set
    var childrenDirNumber //子文件夹数量
            : Int? = null
        private set
    var boxVisible //checkbox是否显示
            : Boolean? = null
        private set
    var boxChecked //checkbox是否选择
            : Boolean? = null
        private set
    var modifyTime //文件修改时间
            : Long? = null
        private set
    var size //占空间大小.还有一个作用就是如果size=-5411则说明是返回FileBean
            : Long? = null
        private set
    var sizeString //占空间大小.还有一个作用就是如果size=-5411则说明是返回FileBean
            : String? = null
        private set

    /**
     * 通过DocumentFile可以对Android/data目录进行操作
     * 具体怎么操作可以百度
     */
    var documentFile: DocumentFile? = null
        private set
    var useUri //是否使用uri地址
            : Boolean? = null
        private set

    constructor() {}
    constructor(path: String?) {
        this.path = path
    }

    constructor(path: String?, name: String?, size: Long?) {
        this.path = path
        this.name = name
        this.size = size
    }

    constructor(
        path: String?,
        name: String?,
        dir: Boolean?,
        fileExtension: String?,
        fileIcoType: Int?,
        childrenFileNumber: Int?,
        childrenDirNumber: Int?,
        boxVisible: Boolean?,
        boxChecked: Boolean?,
        modifyTime: Long?,
        size: Long?,
        documentFile: DocumentFile?,
        useUri: Boolean?
    ) {
        this.path = path
        this.name = name
        isDir = dir
        this.fileExtension = fileExtension
        this.fileIcoType = fileIcoType
        this.childrenFileNumber = childrenFileNumber
        this.childrenDirNumber = childrenDirNumber
        this.boxVisible = boxVisible
        this.boxChecked = boxChecked
        this.modifyTime = modifyTime
        this.size = size
        this.documentFile = documentFile
        this.useUri = useUri
    }

    fun setPath(path: String?): FileBean {
        this.path = path
        return this
    }

    fun clear(): FileBean {
        path = null
        name = null
        isDir = null
        fileExtension = null
        fileIcoType = null
        childrenFileNumber = null
        childrenDirNumber = null
        boxVisible = null
        boxChecked = null
        modifyTime = null
        size = null
        documentFile = null
        useUri = null
        return this
    }

    fun setName(name: String?): FileBean {
        this.name = name
        return this
    }

    fun setDir(dir: Boolean?): FileBean {
        isDir = dir
        return this
    }

    fun setFileExtension(fileExtension: String?): FileBean {
        this.fileExtension = fileExtension
        return this
    }

    fun setFileIcoType(fileIcoType: Int?): FileBean {
        this.fileIcoType = fileIcoType
        return this
    }

    fun setChildrenFileNumber(childrenFileNumber: Int?): FileBean {
        this.childrenFileNumber = childrenFileNumber
        return this
    }

    fun setChildrenDirNumber(childrenDirNumber: Int?): FileBean {
        this.childrenDirNumber = childrenDirNumber
        return this
    }

    fun setBoxVisible(boxVisible: Boolean?): FileBean {
        this.boxVisible = boxVisible
        return this
    }

    fun setBoxChecked(boxChecked: Boolean?): FileBean {
        this.boxChecked = boxChecked
        return this
    }

    fun setModifyTime(modifyTime: Long?): FileBean {
        this.modifyTime = modifyTime
        return this
    }

    fun setSize(size: Long?): FileBean {
        this.size = size
        return this
    }

    fun setSizeString(sizeString: String?): FileBean {
        this.sizeString = sizeString
        return this
    }

    fun setDocumentFile(documentFile: DocumentFile?): FileBean {
        this.documentFile = documentFile
        return this
    }

    fun setUseUri(useUri: Boolean?): FileBean {
        this.useUri = useUri
        return this
    }

    override fun toString(): String {
        return "FileBean{" +
                "path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", dir=" + isDir +
                ", fileExtension='" + fileExtension + '\'' +
                ", fileIcoType=" + fileIcoType +
                ", childrenFileNumber=" + childrenFileNumber +
                ", childrenDirNumber=" + childrenDirNumber +
                ", boxVisible=" + boxVisible +
                ", boxChecked=" + boxChecked +
                ", modifyTime=" + modifyTime +
                ", size=" + size +
                ", documentFile=" + documentFile +
                ", useUri=" + useUri +
                '}'
    }
}