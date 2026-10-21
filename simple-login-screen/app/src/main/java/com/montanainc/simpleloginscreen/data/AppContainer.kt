package com.montanainc.simpleloginscreen.data

import android.content.Context
import com.montanainc.simpleloginscreen.database.AppDatabase

// Creates and stores dependencies like database and the repo so it is created once for app life

interface AppContainer {
    val userRepository: UserRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    override val userRepository: UserRepository by lazy {
        OfflineUserRepository(AppDatabase.getDatabase(context).userDao())
    }
}