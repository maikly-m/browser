package com.emoji.media.browser.bean

abstract class PathInfo(
    open val fullPath: MutableList<String>,
    open val currentPath: String,
    open val modifiedTime: String,
)
