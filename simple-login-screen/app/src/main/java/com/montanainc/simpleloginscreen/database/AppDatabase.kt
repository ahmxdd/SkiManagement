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


import com.montanainc.simpleloginscreen.BannerDao
import com.montanainc.simpleloginscreen.BookingExtraDao
import com.montanainc.simpleloginscreen.DiscountDao
import com.montanainc.simpleloginscreen.FeedbackDao
import com.montanainc.simpleloginscreen.WaitlistDao
import com.montanainc.simpleloginscreen.entities.AppBanner
import com.montanainc.simpleloginscreen.entities.BookingExtra
import com.montanainc.simpleloginscreen.entities.Discount
import com.montanainc.simpleloginscreen.entities.Feedback
import com.montanainc.simpleloginscreen.entities.WaitlistEntry

@Database(entities = [User::class, Equipment::class, Rental::class, Feedback::class, WaitlistEntry::class, Discount::class, AppBanner::class, BookingExtra::class], version = 3, exportSchema = false) //Version should increase if new columns are added. Export schema is false so that to not keep schema version history backups.
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun equipmentDao(): EquipmentDao
    abstract fun rentalDao(): RentalDao

    abstract fun feedbackDao(): FeedbackDao
    abstract fun waitlistDao(): WaitlistDao
    abstract fun discountDao(): DiscountDao
    abstract fun bannerDao(): BannerDao
    abstract fun bookingExtraDao(): BookingExtraDao

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
                    .addMigrations(MIGRATION_2_3)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}

