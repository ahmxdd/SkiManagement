package com.montanainc.simpleloginscreen.data

import com.montanainc.simpleloginscreen.EquipmentDao
import com.montanainc.simpleloginscreen.entities.Equipment
import kotlinx.coroutines.flow.Flow

class OfflineEquipmentRepository(private val equipmentDao: EquipmentDao): EquipmentRepository {
    override fun getAllVisible(): Flow<List<Equipment>> = equipmentDao.getAllVisible()

    override fun search(query: String): Flow<List<Equipment>> = equipmentDao.search(query)

    override fun getBySkillLevel(level: String): Flow<List<Equipment>> = equipmentDao.getBySkillLevel(level)


    override suspend fun insertEquipment(equipment: Equipment) = equipmentDao.insert(equipment)

    override suspend fun update(equipment: Equipment) = equipmentDao.update(equipment)


    override suspend fun setStatus(id: Int, status: String) = equipmentDao.setStatus(id, status)
}