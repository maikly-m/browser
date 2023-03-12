package com.emoji.media.browser.ui.viewmodel

import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class ContentFragmentVM(application: Application): AndroidViewModel(application) {
    val showSelector = MutableLiveData(false)
    val selectorListSize = MutableLiveData(0)
}