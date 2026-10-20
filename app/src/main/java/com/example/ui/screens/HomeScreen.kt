package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.BirthdayContact
import com.example.ui.components.BalloonIcon
import com.example.ui.components.BirthdayCard
import com.example.ui.components.CakeIcon
import com.example.ui.components.ConfettiMotif
import com.example.ui.components.ContactAvatar
import com.example.ui.components.GiftIcon
import com.example.ui.theme.DarkText
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldAccentDark
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.OnGoldContainer
import com.example.ui.theme.PinkContainer
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.SoftPinkBg
import com.example.ui.theme.SubtitleText
import com.example.util.BirthdayUtils
import com.example.viewmodel.BirthdayViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: BirthdayViewModel,
    onNavigateToAdd: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToContacts: () -> Unit,
    modifier: Modifier = Modifier
) {
    val todayBirthdays by viewModel.todayBirthdays.collectAsStateWithLifecycle()
    val upcomingThisMonth by viewModel.upcomingThisMonth.collectAsStateWithLifecycle()
    val greetingTemplate by viewModel.greetingTemplate.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val today = LocalDate.now()

    Scaffold(
        modifier = modifier.testTag("home_screen"),
        containerColor = SoftPinkBg,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CakeIcon(size = 32.dp)
                        Text(
                            text = "Celebrate",
                            style = MaterialTheme.typography.displayMedium,
                            color = PinkPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToAdd,
                        modifier = Modifier.testTag("home_add_button")
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = PinkPrimary,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Birthday",
                                tint = Color.White,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SoftPinkBg
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAdd,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add Birthday", fontWeight = FontWeight.SemiBold) },
                containerColor = PinkPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("home_fab")
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Highlighted "Today's Birthdays" Banner (if any)
            if (todayBirthdays.isNotEmpty()) {
                item {
                    TodayBirthdaysBanner(
                        celebrants = todayBirthdays,
                        onCelebrateContact = { contact ->
                            onNavigateToDetail(contact.id)
                        },
                        onSendWish = { contact ->
                            val message = greetingTemplate.replace("{name}", contact.name)
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("smsto:${contact.phoneNumber.ifBlank { "" }}")
                                putExtra("sms_body", message)
                            }
                            try {
                                context.startActivity(intent)
                            } catch (_: Exception) {}
                        }
                    )
                }
            }

            // 2. Playful Hero Banner Card
            item {
                HeroFestiveCard(
                    upcomingCount = upcomingThisMonth.size,
                    todayCount = todayBirthdays.size
                )
            }

            // 3. "Upcoming This Month" Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BalloonIcon(size = 24.dp)
                        Text(
                            text = "Upcoming This Month",
                            style = MaterialTheme.typography.titleLarge,
                            color = DarkText,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = PinkContainer
                    ) {
                        Text(
                            text = "${upcomingThisMonth.size} birthdays",
                            style = MaterialTheme.typography.labelSmall,
                            color = PinkPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // 4. Birthday Cards List
            if (upcomingThisMonth.isEmpty()) {
                item {
                    EmptyMonthCard(onAddClicked = onNavigateToAdd)
                }
            } else {
                items(
                    items = upcomingThisMonth,
                    key = { it.id }
                ) { contact ->
                    BirthdayCard(
                        contact = contact,
                        onClick = { onNavigateToDetail(contact.id) },
                        today = today
                    )
                }
            }

            // Bottom spacer for FAB
            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Composable
fun TodayBirthdaysBanner(
    celebrants: List<BirthdayContact>,
    onCelebrateContact: (BirthdayContact) -> Unit,
    onSendWish: (BirthdayContact) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("today_birthdays_banner"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFEF3C7)
        ),
        border = androidx.compose.foundation.BorderStroke(2.dp, GoldAccent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFEF08A).copy(alpha = 0.5f),
                            Color(0xFFFDE047).copy(alpha = 0.25f)
                        )
                    )
                )
                .padding(18.dp)
        ) {
            ConfettiMotif(modifier = Modifier.fillMaxWidth().height(100.dp))

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = GoldAccentDark,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🎉", fontSize = 16.sp)
                        }
                    }

                    Column {
                        Text(
                            text = "TODAY'S CELEBRATION!",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF92400E),
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = if (celebrants.size == 1) "It's someone's special day!" else "${celebrants.size} birthdays today!",
                            style = MaterialTheme.typography.titleMedium,
                            color = DarkText,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                celebrants.forEach { contact ->
                    val upcomingAge = BirthdayUtils.calculateUpcomingAge(contact.birthYear, contact.birthMonth, contact.birthDay)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onCelebrateContact(contact) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ContactAvatar(
                                photoUri = contact.photoUri,
                                name = contact.name,
                                emoji = contact.avatarEmoji,
                                colorHex = contact.favoriteColorHex,
                                size = 46
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = contact.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = DarkText,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (upcomingAge != null) "Turning $upcomingAge today! 🎂" else "Happy Birthday! 🎈",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PinkPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Button(
                                onClick = { onSendWish(contact) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Message,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Wish",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeroFestiveCard(
    upcomingCount: Int,
    todayCount: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_festive_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCE7F3)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Illustrated Banner Image
            Image(
                painter = painterResource(id = R.drawable.celebrate_banner_1792492852360),
                contentDescription = "Celebrate Birthday Banner",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                contentScale = ContentScale.Crop
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatBubble(
                    count = todayCount.toString(),
                    label = "Today",
                    accentColor = GoldAccent
                )

                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .width(1.dp)
                        .background(Color(0xFFF1F5F9))
                )

                StatBubble(
                    count = upcomingCount.toString(),
                    label = "This Month",
                    accentColor = PinkPrimary
                )

                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .width(1.dp)
                        .background(Color(0xFFF1F5F9))
                )

                StatBubble(
                    count = "365",
                    label = "Days of Joy",
                    accentColor = Color(0xFFA855F7)
                )
            }
        }
    }
}

@Composable
fun StatBubble(
    count: String,
    label: String,
    accentColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count,
            style = MaterialTheme.typography.titleLarge,
            color = accentColor,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = SubtitleText,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun EmptyMonthCard(
    onAddClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCE7F3))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CakeIcon(size = 56.dp)

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "No birthdays this month yet!",
                style = MaterialTheme.typography.titleMedium,
                color = DarkText,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Add your friends and family to stay notified ahead of time.",
                style = MaterialTheme.typography.bodySmall,
                color = SubtitleText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            FilledTonalButton(
                onClick = onAddClicked,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = PinkContainer,
                    contentColor = PinkPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Birthday", fontWeight = FontWeight.Bold)
            }
        }
    }
}
