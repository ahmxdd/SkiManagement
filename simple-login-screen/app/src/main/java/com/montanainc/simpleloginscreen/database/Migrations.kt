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

// Adds the booking form's special request and equipment info to rentals.
// Two different "version 3" databases exist (Sprint 4 tables vs. the new rental columns),
// so this migration fills in whichever parts are missing instead of assuming one of them.
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        MIGRATION_2_3.migrate(db) // only CREATE ... IF NOT EXISTS, so safe to re-run
        addColumnIfMissing(db, "rental", "special_request", "TEXT NOT NULL DEFAULT ''")
        addColumnIfMissing(db, "rental", "equipment_info", "TEXT NOT NULL DEFAULT ''")
    }
}

private fun addColumnIfMissing(db: SupportSQLiteDatabase, table: String, column: String, definition: String) {
    var exists = false
    db.query("PRAGMA table_info(`$table`)").use { cursor ->
        val nameIndex = cursor.getColumnIndexOrThrow("name")
        while (cursor.moveToNext()) {
            if (cursor.getString(nameIndex) == column) exists = true
        }
    }
    if (!exists) db.execSQL("ALTER TABLE `$table` ADD COLUMN `$column` $definition")
}
