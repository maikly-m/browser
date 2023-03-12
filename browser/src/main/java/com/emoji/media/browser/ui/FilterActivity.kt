package com.emoji.media.browser.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.emoji.media.browser.Constants
import com.emoji.media.browser.PathBuilder
import com.emoji.media.browser.PathSelector
import com.emoji.media.browser.R

class FilterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        val parcelableExtra = intent?.getSerializableExtra(Constants.PATH_EXTRAS)
        (parcelableExtra as PathBuilder).let {
            PathSelector(it, this@FilterActivity, R.id.filter_activity_fl_main).show()
        }
    }

}