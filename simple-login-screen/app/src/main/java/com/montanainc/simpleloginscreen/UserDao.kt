package com.montanainc.simpleloginscreen

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.montanainc.simpleloginscreen.entities.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getALl(): Flow<List<User>>

    @Query("""
        SELECT * FROM user 
        WHERE first_name LIKE :firstName AND last_name LIKE :lastName AND email LIKE :email LIMIT 1
    """)
    fun getUser(firstName: String, lastName: String, email: String): Flow<User>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: User)

    @Delete
    suspend fun removeUser(user: User)


}