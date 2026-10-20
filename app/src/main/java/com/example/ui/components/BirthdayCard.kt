package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.BirthdayContact
import com.example.ui.theme.CategoryBestie
import com.example.ui.theme.CategoryFamily
import com.example.ui.theme.CategoryFriend
import com.example.ui.theme.CategoryOther
import com.example.ui.theme.CategoryPartner
import com.example.ui.theme.CategoryWork
import com.example.ui.theme.DarkText
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.OnGoldContainer
import com.example.ui.theme.OnPinkContainer
import com.example.ui.theme.PinkContainer
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.SubtitleText
import com.example.util.BirthdayUtils
import java.time.LocalDate

@Composable
fun BirthdayCard(
    contact: BirthdayContact,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    today: LocalDate = LocalDate.now()
) {
    val daysUntil = BirthdayUtils.daysUntilNextBirthday(contact.birthMonth, contact.birthDay, today)
    val isToday = daysUntil == 0
    val formattedDate = BirthdayUtils.formatBirthday(contact.birthMonth, contact.birthDay, null)
    val upcomingAge = BirthdayUtils.calculateUpcomingAge(contact.birthYear, contact.birthMonth, contact.birthDay, today)

    val cardBorderColor = if (isToday) GoldAccent else Color(0xFFFCE7F3)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("birthday_card_${contact.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isToday) Color(0xFFFFFBEB) else Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isToday) 2.dp else 1.dp,
            color = cardBorderColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isToday) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            ContactAvatar(
                photoUri = contact.photoUri,
                name = contact.name,
                emoji = contact.avatarEmoji,
                colorHex = contact.favoriteColorHex,
                size = 54
            )

            Spacer(modifier = Modifier.width(14.dp))

            // Main Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = contact.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = DarkText,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SubtitleText,
                        fontWeight = FontWeight.Medium
                    )

                    if (upcomingAge != null) {
                        Text(
                            text = "• Turning $upcomingAge",
                            style = MaterialTheme.typography.bodySmall,
                            color = PinkPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Tags row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    RelationshipTag(relationship = contact.relationship)

                    DaysBadge(days = daysUntil)
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "View Details",
                tint = Color(0xFFCBD5E1),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun ContactAvatar(
    photoUri: String?,
    name: String,
    emoji: String,
    colorHex: String,
    size: Int = 54,
    modifier: Modifier = Modifier
) {
    val initials = name.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .map { it.first().uppercase() }
        .joinToString("")
        .ifEmpty { "🎉" }

    val parsedColor = try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (_: Exception) {
        PinkPrimary
    }

    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(parsedColor, parsedColor.copy(alpha = 0.75f))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        if (!photoUri.isNullOrBlank()) {
            AsyncImage(
                model = photoUri,
                contentDescription = "$name's photo",
                modifier = Modifier
                    .size(size.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = initials,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = (size * 0.36f).sp
                )
            }
        }
    }
}

@Composable
fun RelationshipTag(relationship: String) {
    val (bgColor, textColor) = when (relationship.lowercase()) {
        "family" -> Pair(Color(0xFFFFE4E6), CategoryFamily)
        "friend" -> Pair(Color(0xFFFEF9C3), Color(0xFF854D0E))
        "work" -> Pair(Color(0xFFE0F2FE), CategoryWork)
        "partner" -> Pair(Color(0xFFF3E8FF), CategoryPartner)
        "bestie" -> Pair(Color(0xFFFCE7F3), CategoryBestie)
        else -> Pair(Color(0xFFCCFBF1), CategoryOther)
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor
    ) {
        Text(
            text = relationship,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun DaysBadge(days: Int) {
    val isToday = days == 0
    val isTomorrow = days == 1

    val bgColor = when {
        isToday -> GoldAccent
        isTomorrow -> PinkContainer
        else -> Color(0xFFF1F5F9)
    }

    val textColor = when {
        isToday -> Color(0xFF78350F)
        isTomorrow -> OnPinkContainer
        else -> SubtitleText
    }

    val badgeText = BirthdayUtils.getDaysBadgeText(days)

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor
    ) {
        Text(
            text = badgeText,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
