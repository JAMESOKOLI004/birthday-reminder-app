package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "birthday_contacts")
data class BirthdayContact(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phoneNumber: String = "",
    val photoUri: String? = null,
    val birthMonth: Int, // 1 - 12
    val birthDay: Int,   // 1 - 31
    val birthYear: Int? = null,
    val relationship: String = "Friend", // Family, Friend, Work, Partner, Bestie, Other
    val reminderLeadDays: Int = 0,       // 0 (On day), 1, 2, 3, 7 (1 week)
    val notes: String = "",
    val giftIdeas: String = "",
    val favoriteColorHex: String = "#EC4899",
    val avatarEmoji: String = "🎂"
)
