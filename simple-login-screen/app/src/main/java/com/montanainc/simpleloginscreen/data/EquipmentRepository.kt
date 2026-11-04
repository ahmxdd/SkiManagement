package com.montanainc.simpleloginscreen.data

import com.montanainc.simpleloginscreen.entities.Equipment
import kotlinx.coroutines.flow.Flow

interface EquipmentRepository {
    fun getAllVisible(): Flow<List<Equipment>>

    fun search(query: String): Flow<List<Equipment>>

    fun getBySkillLevel(level: String): Flow<List<Equipment>>

    suspend fun insertEquipment(equipment: Equipment)

    suspend fun update(equipment: Equipment)

    suspend fun setStatus(id: Int, status: String)
}