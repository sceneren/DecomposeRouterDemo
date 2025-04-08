package com.github.sceneren.decomposerouterdemo

import android.app.Application
import kotlin.system.exitProcess

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            e.printStackTrace()
            exitProcess(0)
        }
    }
}