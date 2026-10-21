package com.montanainc.simpleloginscreen.data

import com.montanainc.simpleloginscreen.UserDao
import com.montanainc.simpleloginscreen.entities.User
import kotlinx.coroutines.flow.Flow

class OfflineUserRepository (private val userDao: UserDao) : UserRepository {
    override fun getAllUsersStream(): Flow<List<User>> = userDao.getALl()

    override fun getUserStream(firstName: String, lastName: String, email: String): Flow<User?> = userDao.getUser(firstName, lastName, email)
    override suspend fun getUserByEmail(
        email: String,
        password: String
    ): User? = userDao.getUserByEmail(email, password)


    override suspend fun insertUser(user: User) = userDao.insertUser(user)

    override suspend fun deleteUser(user: User) = userDao.removeUser(user)
}