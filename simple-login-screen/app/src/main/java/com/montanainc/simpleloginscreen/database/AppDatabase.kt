package com.montanainc.simpleloginscreen.database

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.montanainc.simpleloginscreen.UserDao
import com.montanainc.simpleloginscreen.entities.User

@Database(entities = [User::class], version = 1, exportSchema = false) //Version should increase if new columns are added. Export schema is false so that to not keep schema version history backups.
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile // Makes sure that all instance values are up to date and is same for execution threads.
        private var INSTANCE: AppDatabase? = null // The instance variable keeps a reference to the database when one has been created, helps maintain a single instance of database opened at a given time.

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) { // The synchronized block means that only one thread of execution at a time, so this intends for database to be initialized one and one thread to be run.
                Room.databaseBuilder(context, AppDatabase::class.java, "user_database"
                )
                    .setDriver(AndroidSQLiteDriver())
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}

