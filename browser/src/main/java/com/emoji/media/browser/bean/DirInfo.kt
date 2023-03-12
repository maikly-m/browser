package com.emoji.media.browser.bean

class DirInfo(
    val size: String? = null,
    val list: MutableList<PathInfo> = mutableListOf(),
    var childSize: Int = 0,
    override val fullPath: MutableList<String> = mutableListOf(),
    override val currentPath: String = "",
    override val modifiedTime: String = "",
):PathInfo(fullPath, currentPath, modifiedTime){
    override fun toString(): String {
        return "DirInfo(size=$size, list=$list, childSize=$childSize, fullPath=$fullPath, currentPath='$currentPath', modifiedTime=$modifiedTime)"
    }
}
