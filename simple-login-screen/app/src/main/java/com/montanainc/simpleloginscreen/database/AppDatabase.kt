package com.montanainc.simpleloginscreen.database

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.montanainc.simpleloginscreen.UserDao
import com.montanainc.simpleloginscreen.entities.User

@Database(entities = [User::class], version = 1) //Version should increase if new columns are added
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java, "database-name"
                )
                    .setDriver(AndroidSQLiteDriver())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

