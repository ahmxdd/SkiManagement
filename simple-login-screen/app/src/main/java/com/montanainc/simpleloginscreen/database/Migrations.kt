package com.montanainc.simpleloginscreen.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `feedback` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`equipment_id` INTEGER NOT NULL, `user_id` INTEGER NOT NULL, `rating` INTEGER NOT NULL, " +
                    "`comment` TEXT NOT NULL, `created_at` INTEGER NOT NULL)"
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_feedback_equipment_id` ON `feedback` (`equipment_id`)")

        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `waitlist` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`equipment_id` INTEGER NOT NULL, `user_id` INTEGER NOT NULL, `email` TEXT NOT NULL, " +
                    "`created_at` INTEGER NOT NULL, `notified` INTEGER NOT NULL)"
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_waitlist_equipment_id` ON `waitlist` (`equipment_id`)")

        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `discount` (`code` TEXT NOT NULL, `amount` REAL NOT NULL, " +
                    "`active` INTEGER NOT NULL, PRIMARY KEY(`code`))"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `banner` (`id` INTEGER NOT NULL, `message` TEXT NOT NULL, " +
                    "`is_active` INTEGER NOT NULL, PRIMARY KEY(`id`))"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `booking_extra` (`booking_id` TEXT NOT NULL, `total_cost` REAL NOT NULL, " +
                    "`late_fee` REAL NOT NULL, PRIMARY KEY(`booking_id`))"
        )
    }
}
