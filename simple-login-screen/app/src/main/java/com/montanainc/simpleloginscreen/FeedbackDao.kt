package com.montanainc.simpleloginscreen

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.montanainc.simpleloginscreen.entities.Feedback
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedbackDao {
    @Insert
    suspend fun insert(feedback: Feedback)

    @Query("SELECT COUNT(*) FROM feedback")
    suspend fun count(): Int

    @Query("""
        SELECT f.id AS id, f.equipment_id AS equipment_id, e.name AS item_name,
               f.rating AS rating, f.comment AS comment, f.created_at AS created_at
        FROM feedback f
        LEFT JOIN equipment e ON f.equipment_id = e.id
        ORDER BY f.created_at DESC
    """)
    fun getAllWithItem(): Flow<List<FeedbackDetails>>
}

data class FeedbackDetails(
    val id: Int,
    @ColumnInfo(name = "equipment_id") val equipmentId: Int,
    @ColumnInfo(name = "item_name") val itemName: String?,
    val rating: Int,
    val comment: String,
    @ColumnInfo(name = "created_at") val createdAt: Long
)
