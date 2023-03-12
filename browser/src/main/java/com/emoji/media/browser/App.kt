package com.emoji.media.browser

import android.app.Application
import timber.log.Timber
import timber.log.Timber.DebugTree


class App:Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(LogsTree())
        }
    }
}

class LogsTree : DebugTree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        super.log(priority, "browse:${tag}", message, t)
    }
}