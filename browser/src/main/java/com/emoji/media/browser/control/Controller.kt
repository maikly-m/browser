package com.emoji.media.browser.control

import android.os.Environment
import androidx.lifecycle.LifecycleCoroutineScope
import com.emoji.media.browser.bean.DirInfo
import com.emoji.media.browser.findPathInfo
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface IControl{
    fun back(position:Int)
    fun go(list:MutableList<String>)
    fun init()
    fun exit()
}

class Controller(val pathList: MutableList<String>,
                 private val dirFilter: List<String>?,
                 private val fileFilter: List<String>?,
                 private val coroutineScope: LifecycleCoroutineScope,
                 private val exitFunc:(Controller)->Unit) : IControl{

    private val pathChangeListenerMap = hashMapOf<String, (DirInfo, Boolean)->Unit>()
    var dirInfo = DirInfo()
        private set
    val originPaths: ArrayList<String> = arrayListOf()
    var selectFile: ((ArrayList<String>)->Unit)? = null

    init {
        Environment.getExternalStorageDirectory().path.split("/").filter {
            return@filter it != ""
        }.forEach {
            originPaths.add(it)
        }
    }

    override fun back(position: Int) {
        if (position > pathList.size || position < originPaths.size){
            //default
            pathList.clear()
            pathList.addAll(originPaths)
        }else{
            val mutableListOf = mutableListOf<String>()
            kotlin.run {
                pathList.forEachIndexed{
                        index, s ->
                    if (index > position){
                        return@run
                    }
                    mutableListOf.add(s)
                }
            }
            pathList.clear()
            pathList.addAll(mutableListOf)
        }
        //notify listener
        pathChangeUpdate()
    }

    override fun go(list:MutableList<String>) {
        pathList.clear()
        pathList.addAll(list)
        pathChangeUpdate()
    }

    override fun init() {
        pathChangeUpdate()
    }

    override fun exit() {
        exitFunc(this)
    }

    private fun pathChangeUpdate() {
        //search
        coroutineScope.launch(Dispatchers.IO, CoroutineStart.DEFAULT){
            findPathInfo(pathList, dirFilter, fileFilter){ info ->
                //Timber.e("add ... ")
                coroutineScope.launch {
                    var changed = false
                    if (dirInfo != info){
                        dirInfo = info
                        changed = true
                    }
                    pathChangeListenerMap.forEach { (_, u) ->
                        u(dirInfo, changed)
                    }
                }
            }
        }
    }

    fun setPathChangeListener(key: String, block: (DirInfo, Boolean)->Unit){
        pathChangeListenerMap[key] = block
    }

}