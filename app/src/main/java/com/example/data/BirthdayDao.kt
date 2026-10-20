package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BirthdayDao {
    @Query("SELECT * FROM birthday_contacts ORDER BY name ASC")
    fun getAllContacts(): Flow<List<BirthdayContact>>

    @Query("SELECT * FROM birthday_contacts WHERE id = :id")
    fun getContactById(id: Long): Flow<BirthdayContact?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: BirthdayContact): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(contacts: List<BirthdayContact>)

    @Update
    suspend fun updateContact(contact: BirthdayContact)

    @Delete
    suspend fun deleteContact(contact: BirthdayContact)

    @Query("DELETE FROM birthday_contacts WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM birthday_contacts")
    suspend fun getCount(): Int

    @Query("DELETE FROM birthday_contacts")
    suspend fun deleteAll()
}
