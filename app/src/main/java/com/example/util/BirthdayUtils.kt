package com.example.util

import java.time.LocalDate
import java.time.Month
import java.time.temporal.ChronoUnit

object BirthdayUtils {
    fun daysUntilNextBirthday(month: Int, day: Int, today: LocalDate = LocalDate.now()): Int {
        val currentYear = today.year
        val validDay = when (month) {
            2 -> if (day > 28) (if (LocalDate.of(currentYear, 1, 1).isLeapYear) 29 else 28) else day
            4, 6, 9, 11 -> day.coerceAtMost(30)
            else -> day.coerceAtMost(31)
        }

        val thisYearBday = try {
            LocalDate.of(currentYear, month.coerceIn(1, 12), validDay)
        } catch (_: Exception) {
            LocalDate.of(currentYear, month.coerceIn(1, 12), 28)
        }

        val nextBday = when {
            thisYearBday.isEqual(today) -> thisYearBday
            thisYearBday.isAfter(today) -> thisYearBday
            else -> {
                val nextYear = currentYear + 1
                val nextDay = when (month) {
                    2 -> if (day > 28) (if (LocalDate.of(nextYear, 1, 1).isLeapYear) 29 else 28) else day
                    4, 6, 9, 11 -> day.coerceAtMost(30)
                    else -> day.coerceAtMost(31)
                }
                LocalDate.of(nextYear, month.coerceIn(1, 12), nextDay)
            }
        }
        return ChronoUnit.DAYS.between(today, nextBday).toInt()
    }

    fun isBirthdayToday(month: Int, day: Int, today: LocalDate = LocalDate.now()): Boolean {
        return today.monthValue == month && today.dayOfMonth == day
    }

    fun isBirthdayThisMonth(month: Int, today: LocalDate = LocalDate.now()): Boolean {
        return today.monthValue == month
    }

    fun calculateUpcomingAge(birthYear: Int?, month: Int, day: Int, today: LocalDate = LocalDate.now()): Int? {
        if (birthYear == null || birthYear <= 0) return null
        val daysUntil = daysUntilNextBirthday(month, day, today)
        val celebrationYear = if (daysUntil == 0) {
            today.year
        } else if (month < today.monthValue || (month == today.monthValue && day < today.dayOfMonth)) {
            today.year + 1
        } else {
            today.year
        }
        return (celebrationYear - birthYear).coerceAtLeast(0)
    }

    fun calculateCurrentAge(birthYear: Int?, month: Int, day: Int, today: LocalDate = LocalDate.now()): Int? {
        if (birthYear == null || birthYear <= 0) return null
        val birthDate = try {
            LocalDate.of(birthYear, month.coerceIn(1, 12), day.coerceIn(1, 28))
        } catch (_: Exception) {
            return null
        }
        return ChronoUnit.YEARS.between(birthDate, today).toInt().coerceAtLeast(0)
    }

    fun formatBirthday(month: Int, day: Int, year: Int?): String {
        val monthName = Month.of(month.coerceIn(1, 12)).name.lowercase().replaceFirstChar { it.uppercase() }
        return if (year != null && year > 0) {
            "$monthName $day, $year"
        } else {
            "$monthName $day"
        }
    }

    fun getDaysBadgeText(days: Int): String {
        return when (days) {
            0 -> "Today! 🎉"
            1 -> "Tomorrow! 🎂"
            else -> "In $days days"
        }
    }

    fun getZodiacSign(month: Int, day: Int): String {
        return when (month) {
            1 -> if (day < 20) "Capricorn ♑" else "Aquarius ♒"
            2 -> if (day < 19) "Aquarius ♒" else "Pisces ♓"
            3 -> if (day < 21) "Pisces ♓" else "Aries ♈"
            4 -> if (day < 20) "Aries ♈" else "Taurus ♉"
            5 -> if (day < 21) "Taurus ♉" else "Gemini ♊"
            6 -> if (day < 21) "Gemini ♊" else "Cancer ♋"
            7 -> if (day < 23) "Cancer ♋" else "Leo ♌"
            8 -> if (day < 23) "Leo ♌" else "Virgo ♍"
            9 -> if (day < 23) "Virgo ♍" else "Libra ♎"
            10 -> if (day < 23) "Libra ♎" else "Scorpio ♏"
            11 -> if (day < 22) "Scorpio ♏" else "Sagittarius ♐"
            12 -> if (day < 22) "Sagittarius ♐" else "Capricorn ♑"
            else -> "Celebration ⭐"
        }
    }
}
