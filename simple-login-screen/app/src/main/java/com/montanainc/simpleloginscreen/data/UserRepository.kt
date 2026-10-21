package com.montanainc.simpleloginscreen.data

import com.montanainc.simpleloginscreen.entities.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getAllUsersStream(): Flow<List<User>>

    fun getUserStream(firstName: String, lastName: String, email: String): Flow<User?>

    suspend fun getUserByEmail(email: String, password: String): User?
    suspend fun insertUser(user: User)

    suspend fun deleteUser(user: User)




}