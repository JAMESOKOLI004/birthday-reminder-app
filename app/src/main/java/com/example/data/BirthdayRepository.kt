package com.example.data

import kotlinx.coroutines.flow.Flow

class BirthdayRepository(private val dao: BirthdayDao) {
    val allContacts: Flow<List<BirthdayContact>> = dao.getAllContacts()

    fun getContactById(id: Long): Flow<BirthdayContact?> = dao.getContactById(id)

    suspend fun insertContact(contact: BirthdayContact): Long = dao.insertContact(contact)

    suspend fun updateContact(contact: BirthdayContact) = dao.updateContact(contact)

    suspend fun deleteContact(contact: BirthdayContact) = dao.deleteContact(contact)

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    suspend fun resetWithSampleData() {
        dao.deleteAll()
        AppDatabase.populateInitialData(dao)
    }
}
