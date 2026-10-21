package com.montanainc.simpleloginscreen.data

import android.app.Application

class UserApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}