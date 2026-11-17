package com.montanainc.simpleloginscreen

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.montanainc.simpleloginscreen.entities.AppBanner
import kotlinx.coroutines.flow.Flow

@Dao
interface BannerDao {
    @Query("SELECT * FROM banner WHERE id = 1")
    fun observe(): Flow<AppBanner?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(banner: AppBanner)
}
