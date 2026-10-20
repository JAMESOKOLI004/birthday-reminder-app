package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.BirthdayContact
import com.example.ui.components.BalloonIcon
import com.example.ui.components.CakeIcon
import com.example.ui.components.DaysBadge
import com.example.ui.components.GiftIcon
import com.example.ui.components.RelationshipTag
import com.example.ui.theme.DarkText
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.OnGoldContainer
import com.example.ui.theme.PinkContainer
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.SoftPinkBg
import com.example.ui.theme.SubtitleText
import com.example.util.BirthdayUtils
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailScreen(
    contact: BirthdayContact?,
    onBack: () -> Unit,
    onEdit: (BirthdayContact) -> Unit,
    onDelete: (BirthdayContact) -> Unit,
    onUpdateGiftIdeas: (BirthdayContact, String) -> Unit,
    greetingTemplate: String,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var newGiftIdeaText by remember { mutableStateOf("") }
    var isAddingGiftIdea by remember { mutableStateOf(false) }

    if (contact == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(SoftPinkBg),
            contentAlignment = Alignment.Center
        ) {
            Text("Contact not found", style = MaterialTheme.typography.titleMedium, color = SubtitleText)
        }
        return
    }

    val today = LocalDate.now()
    val daysUntil = BirthdayUtils.daysUntilNextBirthday(contact.birthMonth, contact.birthDay, today)
    val upcomingAge = BirthdayUtils.calculateUpcomingAge(contact.birthYear, contact.birthMonth, contact.birthDay, today)
    val currentAge = BirthdayUtils.calculateCurrentAge(contact.birthYear, contact.birthMonth, contact.birthDay, today)
    val zodiac = BirthdayUtils.getZodiacSign(contact.birthMonth, contact.birthDay)
    val formattedBirthday = BirthdayUtils.formatBirthday(contact.birthMonth, contact.birthDay, contact.birthYear)

    val customGreeting = greetingTemplate.replace("{name}", contact.name)

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Delete Birthday?") },
            text = { Text("Are you sure you want to remove ${contact.name}'s birthday from Celebrate?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDelete(contact)
                    }
                ) {
                    Text("Delete", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.testTag("contact_detail_screen"),
        containerColor = SoftPinkBg,
        topBar = {
            TopAppBar(
                title = { Text("Celebrant Details", fontWeight = FontWeight.Bold, color = DarkText) },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("detail_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkText)
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onEdit(contact) },
                        modifier = Modifier.testTag("detail_edit_button")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Contact", tint = PinkPrimary)
                    }
                    IconButton(
                        onClick = { showDeleteConfirmDialog = true },
                        modifier = Modifier.testTag("detail_delete_button")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Contact", tint = Color(0xFFEF4444))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SoftPinkBg)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Large Avatar Card with Hero Styling
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFCE7F3)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val avatarColor = try {
                        Color(android.graphics.Color.parseColor(contact.favoriteColorHex))
                    } catch (_: Exception) {
                        PinkPrimary
                    }

                    // Large Avatar
                    Box(
                        modifier = Modifier
                            .size(108.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(avatarColor.copy(alpha = 0.9f), avatarColor)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!contact.photoUri.isNullOrBlank()) {
                            AsyncImage(
                                model = contact.photoUri,
                                contentDescription = contact.name,
                                modifier = Modifier
                                    .size(108.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = contact.name.split(" ").take(2).map { it.first().uppercase() }.joinToString("").ifEmpty { contact.avatarEmoji },
                                color = Color.White,
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = contact.name,
                        style = MaterialTheme.typography.headlineLarge,
                        color = DarkText,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RelationshipTag(relationship = contact.relationship)

                        DaysBadge(days = daysUntil)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Age and Birthday highlight pill
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFFFF1F2),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECDD3)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = formattedBirthday,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PinkPrimary
                                )
                                Text(
                                    text = zodiac,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SubtitleText
                                )
                            }

                            if (upcomingAge != null) {
                                Box(
                                    modifier = Modifier
                                        .height(30.dp)
                                        .width(1.dp)
                                        .background(Color(0xFFFECDD3))
                                )

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = if (daysUntil == 0) "$upcomingAge years" else "Turning $upcomingAge",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldAccent
                                    )
                                    Text(
                                        text = if (currentAge != null) "Currently $currentAge" else "Upcoming Age",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SubtitleText
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick Actions: Call, Message, Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Call Action
                FilledTonalButton(
                    onClick = {
                        val phone = contact.phoneNumber.ifBlank { "" }
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:$phone")
                        }
                        try {
                            context.startActivity(intent)
                        } catch (_: Exception) {}
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("quick_action_call"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color(0xFFDCFCE7),
                        contentColor = Color(0xFF166534)
                    )
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Call", fontWeight = FontWeight.Bold)
                }

                // Message Action
                Button(
                    onClick = {
                        val phone = contact.phoneNumber.ifBlank { "" }
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("smsto:$phone")
                            putExtra("sms_body", customGreeting)
                        }
                        try {
                            context.startActivity(intent)
                        } catch (_: Exception) {}
                    },
                    modifier = Modifier
                        .weight(1.3f)
                        .height(52.dp)
                        .testTag("quick_action_message"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PinkPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Wish Happy Bday", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            // Gift Ideas Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCE7F3))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            GiftIcon(size = 24.dp)
                            Text(
                                text = "Gift Ideas & Wishlist",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )
                        }

                        IconButton(
                            onClick = { isAddingGiftIdea = !isAddingGiftIdea },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Gift Idea", tint = PinkPrimary)
                        }
                    }

                    if (isAddingGiftIdea) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = newGiftIdeaText,
                                onValueChange = { newGiftIdeaText = it },
                                placeholder = { Text("Add idea (e.g. Perfume, mug)...") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                            IconButton(
                                onClick = {
                                    if (newGiftIdeaText.isNotBlank()) {
                                        val updated = if (contact.giftIdeas.isBlank()) {
                                            newGiftIdeaText.trim()
                                        } else {
                                            "${contact.giftIdeas.trim()}, ${newGiftIdeaText.trim()}"
                                        }
                                        onUpdateGiftIdeas(contact, updated)
                                        newGiftIdeaText = ""
                                        isAddingGiftIdea = false
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Save", tint = PinkPrimary)
                            }
                        }
                    }

                    if (contact.giftIdeas.isNotBlank()) {
                        val ideas = contact.giftIdeas.split(",", "\n").map { it.trim() }.filter { it.isNotBlank() }
                        ideas.forEach { idea ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFFFFBEB), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("🎁", fontSize = 14.sp)
                                Text(
                                    text = idea,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = DarkText,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "No gift ideas saved yet. Tap + above to add gift inspiration!",
                            style = MaterialTheme.typography.bodySmall,
                            color = SubtitleText
                        )
                    }
                }
            }

            // Notes Section
            if (contact.notes.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCE7F3))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("📝", fontSize = 20.sp)
                            Text(
                                text = "Personal Notes",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )
                        }

                        Text(
                            text = contact.notes,
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkText,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            // Reminder Setting Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCE7F3))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = GoldContainer,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = OnGoldContainer, modifier = Modifier.size(20.dp))
                        }
                    }

                    Column {
                        val leadLabel = when (contact.reminderLeadDays) {
                            0 -> "On the day of birthday"
                            1 -> "1 day before"
                            2 -> "2 days before"
                            3 -> "3 days before"
                            7 -> "1 week before"
                            else -> "${contact.reminderLeadDays} days before"
                        }
                        Text(
                            text = "Reminder Scheduled",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                        Text(
                            text = leadLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = SubtitleText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
