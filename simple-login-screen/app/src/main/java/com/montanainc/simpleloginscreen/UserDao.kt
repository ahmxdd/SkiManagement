package com.montanainc.simpleloginscreen

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.montanainc.simpleloginscreen.entities.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getALl(): Flow<List<User>>

    @Query("""
        SELECT * FROM users
        WHERE first_name LIKE :firstName AND last_name LIKE :lastName AND email LIKE :email LIMIT 1
    """)
    fun getUser(firstName: String, lastName: String, email: String): Flow<User>

    @Query("""
        SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1
    """)
    suspend fun getUserByEmail(email: String, password: String): User?
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: User)

    @Delete
    suspend fun removeUser(user: User)




}