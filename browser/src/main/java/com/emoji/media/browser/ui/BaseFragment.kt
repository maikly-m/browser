package com.emoji.media.browser.ui

import androidx.fragment.app.Fragment
import com.emoji.media.browser.control.Controller

abstract class BaseFragment: Fragment() {
    var controller:Controller? = null
}