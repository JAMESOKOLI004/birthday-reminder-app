package com.example

import com.example.util.BirthdayUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class BirthdayUtilsTest {

    @Test
    fun testBirthdayToday() {
        val today = LocalDate.of(2026, 10, 20)
        assertTrue(BirthdayUtils.isBirthdayToday(10, 20, today))
        assertFalse(BirthdayUtils.isBirthdayToday(10, 21, today))
        assertEquals(0, BirthdayUtils.daysUntilNextBirthday(10, 20, today))
    }

    @Test
    fun testDaysUntilNextBirthday() {
        val today = LocalDate.of(2026, 10, 20)
        assertEquals(1, BirthdayUtils.daysUntilNextBirthday(10, 21, today))
        assertEquals(5, BirthdayUtils.daysUntilNextBirthday(10, 25, today))
    }

    @Test
    fun testAgeCalculations() {
        val today = LocalDate.of(2026, 10, 20)
        // Turning 26 today (born 2000-10-20)
        val upcomingAgeToday = BirthdayUtils.calculateUpcomingAge(2000, 10, 20, today)
        assertEquals(26, upcomingAgeToday)

        // Born in December 2000 (hasn't happened yet in 2026, turning 26)
        val upcomingAgeDec = BirthdayUtils.calculateUpcomingAge(2000, 12, 1, today)
        assertEquals(26, upcomingAgeDec)

        // Born in January 2000 (already happened in 2026, next birthday is 2027 turning 27)
        val upcomingAgeJan = BirthdayUtils.calculateUpcomingAge(2000, 1, 15, today)
        assertEquals(27, upcomingAgeJan)
    }

    @Test
    fun testZodiacSign() {
        assertEquals("Scorpio ♏", BirthdayUtils.getZodiacSign(10, 24))
        assertEquals("Libra ♎", BirthdayUtils.getZodiacSign(10, 15))
        assertEquals("Leo ♌", BirthdayUtils.getZodiacSign(8, 10))
    }

    @Test
    fun testDaysBadgeText() {
        assertEquals("Today! 🎉", BirthdayUtils.getDaysBadgeText(0))
        assertEquals("Tomorrow! 🎂", BirthdayUtils.getDaysBadgeText(1))
        assertEquals("In 4 days", BirthdayUtils.getDaysBadgeText(4))
    }
}
