package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.BirthdayContact
import com.example.data.BirthdayRepository
import com.example.util.BirthdayUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class BirthdayViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BirthdayRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = BirthdayRepository(db.birthdayDao())

        // Check if database needs immediate sample population on first run
        viewModelScope.launch {
            if (db.birthdayDao().getCount() == 0) {
                AppDatabase.populateInitialData(db.birthdayDao())
            }
        }
    }

    val allContacts: StateFlow<List<BirthdayContact>> = repository.allContacts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow("All")
    val selectedCategoryFilter: StateFlow<String> = _selectedCategoryFilter.asStateFlow()

    private val _selectedCalendarMonth = MutableStateFlow(YearMonth.now())
    val selectedCalendarMonth: StateFlow<YearMonth> = _selectedCalendarMonth.asStateFlow()

    private val _selectedCalendarDate = MutableStateFlow(LocalDate.now())
    val selectedCalendarDate: StateFlow<LocalDate> = _selectedCalendarDate.asStateFlow()

    private val _defaultReminderLeadDays = MutableStateFlow(0)
    val defaultReminderLeadDays: StateFlow<Int> = _defaultReminderLeadDays.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private val _greetingTemplate = MutableStateFlow("Happy Birthday {name}! 🎂🎈 Wishing you an amazing day filled with joy, surprises, and laughter!")
    val greetingTemplate: StateFlow<String> = _greetingTemplate.asStateFlow()

    // Derived states
    val todayBirthdays: StateFlow<List<BirthdayContact>> = allContacts.combine(_selectedCalendarDate) { contacts, _ ->
        val today = LocalDate.now()
        contacts.filter { BirthdayUtils.isBirthdayToday(it.birthMonth, it.birthDay, today) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val upcomingThisMonth: StateFlow<List<BirthdayContact>> = allContacts.combine(_selectedCalendarDate) { contacts, _ ->
        val today = LocalDate.now()
        contacts.filter { it.birthMonth == today.monthValue }
            .sortedBy { BirthdayUtils.daysUntilNextBirthday(it.birthMonth, it.birthDay, today) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val filteredContacts: StateFlow<List<BirthdayContact>> = combine(
        allContacts,
        _searchQuery,
        _selectedCategoryFilter
    ) { contacts, query, category ->
        contacts.filter { contact ->
            val matchesQuery = query.isBlank() ||
                    contact.name.contains(query, ignoreCase = true) ||
                    contact.relationship.contains(query, ignoreCase = true) ||
                    contact.notes.contains(query, ignoreCase = true)
            val matchesCategory = category == "All" || contact.relationship.equals(category, ignoreCase = true)
            matchesQuery && matchesCategory
        }.sortedBy { it.name.lowercase() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategoryFilter(category: String) {
        _selectedCategoryFilter.value = category
    }

    fun setCalendarMonth(yearMonth: YearMonth) {
        _selectedCalendarMonth.value = yearMonth
    }

    fun nextCalendarMonth() {
        _selectedCalendarMonth.value = _selectedCalendarMonth.value.plusMonths(1)
    }

    fun previousCalendarMonth() {
        _selectedCalendarMonth.value = _selectedCalendarMonth.value.minusMonths(1)
    }

    fun setSelectedCalendarDate(date: LocalDate) {
        _selectedCalendarDate.value = date
    }

    fun setDefaultReminderLeadDays(days: Int) {
        _defaultReminderLeadDays.value = days
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
    }

    fun setGreetingTemplate(template: String) {
        _greetingTemplate.value = template
    }

    fun saveContact(contact: BirthdayContact, onSaved: () -> Unit = {}) {
        viewModelScope.launch {
            if (contact.id == 0L) {
                repository.insertContact(contact)
            } else {
                repository.updateContact(contact)
            }
            onSaved()
        }
    }

    fun deleteContact(contact: BirthdayContact, onDeleted: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteContact(contact)
            onDeleted()
        }
    }

    fun resetSampleData() {
        viewModelScope.launch {
            repository.resetWithSampleData()
        }
    }

    fun getCelebrantsForDate(date: LocalDate, contacts: List<BirthdayContact>): List<BirthdayContact> {
        return contacts.filter { it.birthMonth == date.monthValue && it.birthDay == date.dayOfMonth }
    }

    fun getDaysWithBirthdaysInMonth(yearMonth: YearMonth, contacts: List<BirthdayContact>): Set<Int> {
        return contacts.filter { it.birthMonth == yearMonth.monthValue }
            .map { it.birthDay }
            .toSet()
    }
}
