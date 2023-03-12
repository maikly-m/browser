package com.emoji.media.browser

import android.os.Parcelable
import androidx.fragment.app.FragmentActivity
import com.emoji.media.browser.ui.FilterFragment
import kotlinx.parcelize.Parcelize

class PathBuilder : java.io.Serializable {
    var num: Int = 0
    var rootPath: String? = null
    var dirFilter: List<String>? = null
    var fileFilter: List<String>? = null
    var showTitle: Boolean = true
}

class PathSelector(private val pathBuilder: PathBuilder, private val fragmentActivity: FragmentActivity, private val containerId: Int) {
    fun show(){
        fragmentActivity.supportFragmentManager.let {
            val transaction = it.beginTransaction()
            val newInstance = FilterFragment.newInstance(pathBuilder, "")
            transaction.add(
                containerId, newInstance,
                FilterFragment::class.java.simpleName
            )
            transaction.addToBackStack(FilterFragment::class.java.simpleName)
            transaction.commitAllowingStateLoss()
        }
    }
}