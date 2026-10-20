package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

@Database(entities = [BirthdayContact::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun birthdayDao(): BirthdayDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "celebrate_database"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database.birthdayDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: BirthdayDao) {
            val today = LocalDate.now()
            val thisMonth = today.monthValue
            val currentYear = today.year

            val sampleContacts = listOf(
                BirthdayContact(
                    name = "Sophia Martinez",
                    phoneNumber = "555-0192",
                    birthMonth = thisMonth,
                    birthDay = today.dayOfMonth,
                    birthYear = currentYear - 24,
                    relationship = "Bestie",
                    reminderLeadDays = 0,
                    notes = "Loves strawberry matcha latte, film photography, and indie concerts.",
                    giftIdeas = "Instant camera film pack, ceramic floral mug, concert ticket voucher",
                    favoriteColorHex = "#EC4899",
                    avatarEmoji = "🎉"
                ),
                BirthdayContact(
                    name = "Liam Davis",
                    phoneNumber = "555-0144",
                    birthMonth = thisMonth,
                    birthDay = ((today.dayOfMonth + 3 - 1) % 28) + 1,
                    birthYear = currentYear - 29,
                    relationship = "Family",
                    reminderLeadDays = 2,
                    notes = "Huge sci-fi book fan and espresso aficionado.",
                    giftIdeas = "Single-origin Ethiopian coffee beans, leather bookmark, Aeropress filter",
                    favoriteColorHex = "#FBBF24",
                    avatarEmoji = "🚀"
                ),
                BirthdayContact(
                    name = "Emma Johnson",
                    phoneNumber = "555-0178",
                    birthMonth = thisMonth,
                    birthDay = ((today.dayOfMonth + 7 - 1) % 28) + 1,
                    birthYear = currentYear - 27,
                    relationship = "Friend",
                    reminderLeadDays = 1,
                    notes = "Yoga enthusiast, plant mom with 20+ houseplants.",
                    giftIdeas = "Terracotta planter pot, calming lavender candle, organic loose-leaf tea",
                    favoriteColorHex = "#10B981",
                    avatarEmoji = "🌿"
                ),
                BirthdayContact(
                    name = "Noah Kim",
                    phoneNumber = "555-0123",
                    birthMonth = thisMonth,
                    birthDay = ((today.dayOfMonth + 14 - 1) % 28) + 1,
                    birthYear = currentYear - 31,
                    relationship = "Work",
                    reminderLeadDays = 3,
                    notes = "Lead UI designer, mechanical keyboard collector.",
                    giftIdeas = "Custom artisan keycap, wireless desktop charger, desk mat",
                    favoriteColorHex = "#0EA5E9",
                    avatarEmoji = "💻"
                ),
                BirthdayContact(
                    name = "Chloe Dubois",
                    phoneNumber = "555-0189",
                    birthMonth = if (thisMonth == 12) 1 else thisMonth + 1,
                    birthDay = 5,
                    birthYear = currentYear - 26,
                    relationship = "Partner",
                    reminderLeadDays = 7,
                    notes = "Loves gourmet baking, French pastry, and weekend picnics in the botanical gardens.",
                    giftIdeas = "French pastry baking book, marble rolling pin, vintage earrings",
                    favoriteColorHex = "#A855F7",
                    avatarEmoji = "🧁"
                ),
                BirthdayContact(
                    name = "Oliver Smith",
                    phoneNumber = "555-0131",
                    birthMonth = if (thisMonth == 1) 12 else thisMonth - 1,
                    birthDay = 18,
                    birthYear = currentYear - 33,
                    relationship = "Friend",
                    reminderLeadDays = 1,
                    notes = "Board game nights host, hiking and vinyl records.",
                    giftIdeas = "Cooperative board game expansion, vintage vinyl record",
                    favoriteColorHex = "#F97316",
                    avatarEmoji = "🎲"
                ),
                BirthdayContact(
                    name = "Aria Patel",
                    phoneNumber = "555-0165",
                    birthMonth = if (thisMonth == 12) 2 else (thisMonth + 2) % 12 + 1,
                    birthDay = 22,
                    birthYear = currentYear - 23,
                    relationship = "Family",
                    reminderLeadDays = 2,
                    notes = "Architecture student, loves watercolor sketching and city walks.",
                    giftIdeas = "Winsor & Newton watercolor set, moleskine watercolor notebook",
                    favoriteColorHex = "#F43F5E",
                    avatarEmoji = "🎨"
                )
            )

            dao.insertAll(sampleContacts)
        }
    }
}
