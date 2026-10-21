package com.montanainc.simpleloginscreen.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.montanainc.simpleloginscreen.UserDao
import com.montanainc.simpleloginscreen.RentalDao
import com.montanainc.simpleloginscreen.EquipmentDao
import com.montanainc.simpleloginscreen.entities.User
import com.montanainc.simpleloginscreen.entities.Equipment
import com.montanainc.simpleloginscreen.entities.Rental

@Database(entities = [User::class, Equipment::class, Rental::class], version = 2, exportSchema = false) //Version should increase if new columns are added. Export schema is false so that to not keep schema version history backups.
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun equipmentDao(): EquipmentDao
    abstract fun rentalDao(): RentalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "user_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}

