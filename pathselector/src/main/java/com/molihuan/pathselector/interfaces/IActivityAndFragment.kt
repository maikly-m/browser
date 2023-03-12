package com.molihuan.pathselector.interfaces

/**
 * @ClassName IActivityAndFragment
 * @Description Activity与Fragment通信接口
 * @Author molihuan
 * @Date 2022/11/22 12:57
 */
interface IActivityAndFragment {
    operator fun invoke(data: Map<*, *>?): Map<*, *>?
}