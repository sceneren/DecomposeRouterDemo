package com.github.sceneren.decomposerouterdemo

import android.app.Application
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import kotlin.system.exitProcess

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        XLog.init(LogLevel.ALL)
    }
}