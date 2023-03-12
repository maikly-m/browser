package com.emoji.media.jna_demo

import com.sun.jna.Library
import com.sun.jna.Native

val nativeLib: NativeLib = Native.load("jna_demo", NativeLib::class.java) as NativeLib
interface NativeLib : Library {
    //此方法为链接库中的方法
    fun SayTest(i:Int): Int
}