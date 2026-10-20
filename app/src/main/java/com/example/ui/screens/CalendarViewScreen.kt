package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.BirthdayContact
import com.example.ui.components.CakeIcon
import com.example.ui.components.ContactAvatar
import com.example.ui.components.RelationshipTag
import com.example.ui.theme.DarkText
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldAccentDark
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.PinkContainer
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.SoftPinkBg
import com.example.ui.theme.SubtitleText
import com.example.util.BirthdayUtils
import com.example.viewmodel.BirthdayViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarViewScreen(
    viewModel: BirthdayViewModel,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToAddWithDate: (month: Int, day: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val allContacts by viewModel.allContacts.collectAsStateWithLifecycle()
    val selectedMonth by viewModel.selectedCalendarMonth.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedCalendarDate.collectAsStateWithLifecycle()
    val greetingTemplate by viewModel.greetingTemplate.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val today = LocalDate.now()

    // Days in this month with birthdays
    val birthdayDaysInMonth = remember(selectedMonth, allContacts) {
        viewModel.getDaysWithBirthdaysInMonth(selectedMonth, allContacts)
    }

    // Celebrants on selected date
    val celebrantsOnSelectedDate = remember(selectedDate, allContacts) {
        viewModel.getCelebrantsForDate(selectedDate, allContacts)
    }

    val daysOfWeek = listOf(
        DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
    )

    // Calculate grid cells for calendar
    val firstDayOfMonth = selectedMonth.atDay(1)
    val dayOfWeekOfFirstDay = firstDayOfMonth.dayOfWeek
    val leadingEmptyCells = (dayOfWeekOfFirstDay.value % 7) // 0 for Sunday
    val daysInMonth = selectedMonth.lengthOfMonth()

    Scaffold(
        modifier = modifier.testTag("calendar_view_screen"),
        containerColor = SoftPinkBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Birthday Calendar",
                        style = MaterialTheme.typography.headlineLarge,
                        color = DarkText,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    // Jump to today button
                    IconButton(
                        onClick = {
                            viewModel.setCalendarMonth(YearMonth.now())
                            viewModel.setSelectedCalendarDate(LocalDate.now())
                        },
                        modifier = Modifier.testTag("calendar_today_button")
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = PinkContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Today,
                                contentDescription = "Go to Today",
                                tint = PinkPrimary,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SoftPinkBg)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Month Header with arrows
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCE7F3))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Month & Year Nav
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { viewModel.previousCalendarMonth() },
                                modifier = Modifier.size(36.dp).testTag("prev_month_button")
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBackIos, contentDescription = "Previous Month", tint = DarkText, modifier = Modifier.size(16.dp))
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${selectedMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${selectedMonth.year}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkText
                                )
                                Text(
                                    text = "${birthdayDaysInMonth.size} birthdays this month",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PinkPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            IconButton(
                                onClick = { viewModel.nextCalendarMonth() },
                                modifier = Modifier.size(36.dp).testTag("next_month_button")
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = "Next Month", tint = DarkText, modifier = Modifier.size(16.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Days of week header row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            daysOfWeek.forEach { day ->
                                Text(
                                    text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()).take(2),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = SubtitleText,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Calendar Grid
                        val totalCells = leadingEmptyCells + daysInMonth
                        val numRows = (totalCells + 6) / 7

                        for (row in 0 until numRows) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                for (col in 0..6) {
                                    val cellIndex = row * 7 + col
                                    val dayNumber = cellIndex - leadingEmptyCells + 1

                                    if (dayNumber in 1..daysInMonth) {
                                        val cellDate = selectedMonth.atDay(dayNumber)
                                        val hasBirthday = birthdayDaysInMonth.contains(dayNumber)
                                        val isSelected = selectedDate.monthValue == cellDate.monthValue && selectedDate.dayOfMonth == cellDate.dayOfMonth
                                        val isCurrentDay = today.monthValue == cellDate.monthValue && today.dayOfMonth == cellDate.dayOfMonth && today.year == cellDate.year

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .padding(2.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(
                                                    when {
                                                        isSelected -> PinkPrimary
                                                        isCurrentDay -> Color(0xFFFEF3C7)
                                                        else -> Color.Transparent
                                                    }
                                                )
                                                .border(
                                                    width = if (isCurrentDay && !isSelected) 1.5.dp else 0.dp,
                                                    color = if (isCurrentDay && !isSelected) GoldAccent else Color.Transparent,
                                                    shape = RoundedCornerShape(10.dp)
                                                )
                                                .clickable {
                                                    viewModel.setSelectedCalendarDate(cellDate)
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = dayNumber.toString(),
                                                    fontSize = 13.sp,
                                                    fontWeight = if (isSelected || isCurrentDay || hasBirthday) FontWeight.Bold else FontWeight.Normal,
                                                    color = when {
                                                        isSelected -> Color.White
                                                        isCurrentDay -> Color(0xFF78350F)
                                                        hasBirthday -> DarkText
                                                        else -> DarkText.copy(alpha = 0.8f)
                                                    }
                                                )

                                                // Birthday Indicator Dots
                                                if (hasBirthday) {
                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                                                        modifier = Modifier.padding(top = 1.dp)
                                                    ) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(5.dp)
                                                                .clip(CircleShape)
                                                                .background(if (isSelected) GoldAccent else PinkPrimary)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Who's Celebrating Section Header
            item {
                val formattedSelectedDay = "${selectedDate.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${selectedDate.dayOfMonth}"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CakeIcon(size = 22.dp)
                        Text(
                            text = "Celebrating on $formattedSelectedDay",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                    }

                    if (celebrantsOnSelectedDate.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = GoldContainer
                        ) {
                            Text(
                                text = "${celebrantsOnSelectedDate.size} celebrants",
                                color = GoldAccentDark,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Celebrants List or Empty State
            if (celebrantsOnSelectedDate.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCE7F3))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("🎂", fontSize = 34.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No birthdays on this day",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Know someone born on this date?",
                                style = MaterialTheme.typography.bodySmall,
                                color = SubtitleText
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            FilledTonalButton(
                                onClick = {
                                    onNavigateToAddWithDate(selectedDate.monthValue, selectedDate.dayOfMonth)
                                },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = PinkContainer,
                                    contentColor = PinkPrimary
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Add Birthday for this date", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            } else {
                items(
                    items = celebrantsOnSelectedDate,
                    key = { it.id }
                ) { contact ->
                    val upcomingAge = BirthdayUtils.calculateUpcomingAge(contact.birthYear, contact.birthMonth, contact.birthDay)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToDetail(contact.id) },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCE7F3)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ContactAvatar(
                                photoUri = contact.photoUri,
                                name = contact.name,
                                emoji = contact.avatarEmoji,
                                colorHex = contact.favoriteColorHex,
                                size = 48
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = contact.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkText
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    RelationshipTag(relationship = contact.relationship)

                                    if (upcomingAge != null) {
                                        Text(
                                            text = "Turning $upcomingAge",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = PinkPrimary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }

                            Button(
                                onClick = {
                                    val message = greetingTemplate.replace("{name}", contact.name)
                                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("smsto:${contact.phoneNumber.ifBlank { "" }}")
                                        putExtra("sms_body", message)
                                    }
                                    try {
                                        context.startActivity(intent)
                                    } catch (_: Exception) {}
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Message, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Wish", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
