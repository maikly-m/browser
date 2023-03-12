package com.molihuan.pathselector

import android.content.Context
import androidx.fragment.app.Fragment
import com.molihuan.pathselector.PathSelector
import com.molihuan.pathselector.configs.PathSelectorConfig
import com.molihuan.pathselector.service.IConfigDataBuilder
import com.molihuan.pathselector.service.impl.ConfigDataBuilderImpl
import java.util.*

/**
 * @ClassName: PathSelector
 * @Author: molihuan
 * @Date: 2022/11/22/22:09
 * @Description: 以数据为驱动, 先初始化数据，再由数据控制构建
 * 1、初始化DataBuilder(数据构建者)
 * 2、再由DataBuilder初始化AbstractBuildController(构建控制者)并构建
 */
object PathSelector {
    @JvmStatic
    var fragment: Fragment? = null
        private set

    fun setDebug(`var`: Boolean) {
        PathSelectorConfig.setDebug(`var`)
    }

    fun build(fragment: Fragment, buildType: Int): IConfigDataBuilder {
        PathSelector.fragment = fragment
        val context = fragment.context
        Objects.requireNonNull(context, "context is null")
        return finalBuild(context, buildType)
    }

    fun build(context: Context?, buildType: Int): IConfigDataBuilder {
        fragment = null
        return finalBuild(context, buildType)
    }

    private fun finalBuild(context: Context?, buildType: Int): IConfigDataBuilder =
        ConfigDataBuilderImpl.getInstance().apply {
            setContext(context)
            setBuildType(buildType)
        }

}