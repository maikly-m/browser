package com.emoji.media.browser.bean

class FileInfo(
    val size: String? = null,
    val type: String = "",
    override val fullPath: MutableList<String> = mutableListOf(),
    override val currentPath: String = "",
    override val modifiedTime: String = "",
):PathInfo(fullPath, currentPath, modifiedTime){
    override fun toString(): String {
        return "FileInfo(size=$size, type='$type', fullPath=$fullPath, currentPath='$currentPath', modifiedTime=$modifiedTime)"
    }
}
