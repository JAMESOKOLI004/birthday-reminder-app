package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.BirthdayContact
import com.example.ui.theme.DarkText
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.PinkContainer
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.SoftPinkBg
import com.example.ui.theme.SubtitleText
import java.time.LocalDate
import java.time.Month

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditBirthdayScreen(
    initialContact: BirthdayContact? = null,
    onSave: (BirthdayContact) -> Unit,
    onDelete: ((BirthdayContact) -> Unit)? = null,
    onBack: () -> Unit,
    defaultLeadDays: Int = 0,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val isEditing = initialContact != null

    var name by remember { mutableStateOf(initialContact?.name ?: "") }
    var phoneNumber by remember { mutableStateOf(initialContact?.phoneNumber ?: "") }
    var photoUri by remember { mutableStateOf(initialContact?.photoUri) }
    var birthMonth by remember { mutableIntStateOf(initialContact?.birthMonth ?: LocalDate.now().monthValue) }
    var birthDay by remember { mutableIntStateOf(initialContact?.birthDay ?: LocalDate.now().dayOfMonth) }
    var includeYear by remember { mutableStateOf(initialContact?.birthYear != null) }
    var birthYearText by remember { mutableStateOf(initialContact?.birthYear?.toString() ?: (LocalDate.now().year - 25).toString()) }
    var relationship by remember { mutableStateOf(initialContact?.relationship ?: "Friend") }
    var reminderLeadDays by remember { mutableIntStateOf(initialContact?.reminderLeadDays ?: defaultLeadDays) }
    var notes by remember { mutableStateOf(initialContact?.notes ?: "") }
    var giftIdeas by remember { mutableStateOf(initialContact?.giftIdeas ?: "") }
    var favoriteColorHex by remember { mutableStateOf(initialContact?.favoriteColorHex ?: "#EC4899") }
    var avatarEmoji by remember { mutableStateOf(initialContact?.avatarEmoji ?: "🎂") }

    var nameError by remember { mutableStateOf(false) }

    // Modern photo picker using zero-permission PickVisualMedia
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                photoUri = uri.toString()
            }
        }
    )

    val relationshipCategories = listOf("Family", "Friend", "Bestie", "Partner", "Work", "Other")
    val leadTimeOptions = listOf(
        Pair(0, "On the day"),
        Pair(1, "1 day before"),
        Pair(2, "2 days before"),
        Pair(3, "3 days before"),
        Pair(7, "1 week before")
    )
    val colorOptions = listOf("#EC4899", "#FBBF24", "#10B981", "#0EA5E9", "#A855F7", "#F43F5E")
    val emojiOptions = listOf("🎂", "🎈", "🎁", "🎉", "👑", "✨", "🍕", "🌸", "🚀", "🎸")

    Scaffold(
        modifier = modifier.testTag("add_edit_screen"),
        containerColor = SoftPinkBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Edit Birthday" else "Add Birthday",
                        style = MaterialTheme.typography.titleLarge,
                        color = DarkText,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = DarkText
                        )
                    }
                },
                actions = {
                    if (isEditing && onDelete != null && initialContact != null) {
                        IconButton(
                            onClick = { onDelete(initialContact) },
                            modifier = Modifier.testTag("delete_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Contact",
                                tint = Color(0xFFEF4444)
                            )
                        }
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
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Photo & Avatar Picker Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCE7F3))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .clip(CircleShape)
                            .background(
                                try {
                                    Color(android.graphics.Color.parseColor(favoriteColorHex))
                                } catch (_: Exception) {
                                    PinkPrimary
                                }
                            )
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                            .testTag("photo_picker_avatar"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!photoUri.isNullOrBlank()) {
                            AsyncImage(
                                model = photoUri,
                                contentDescription = "Contact photo",
                                modifier = Modifier
                                    .size(92.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = avatarEmoji,
                                fontSize = 42.sp
                            )
                        }

                        // Camera overlay badge
                        Surface(
                            shape = CircleShape,
                            color = PinkPrimary,
                            modifier = Modifier
                                .size(30.dp)
                                .align(Alignment.BottomEnd)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AddAPhoto,
                                    contentDescription = "Upload photo",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (photoUri != null) "Tap photo to change" else "Upload photo or pick avatar emoji",
                        style = MaterialTheme.typography.bodySmall,
                        color = SubtitleText
                    )

                    // Emoji choices
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        emojiOptions.take(6).forEach { emoji ->
                            Surface(
                                shape = CircleShape,
                                color = if (avatarEmoji == emoji && photoUri == null) PinkContainer else Color(0xFFF8FAFC),
                                border = if (avatarEmoji == emoji && photoUri == null) androidx.compose.foundation.BorderStroke(2.dp, PinkPrimary) else null,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable {
                                        avatarEmoji = emoji
                                        photoUri = null
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(emoji, fontSize = 18.sp)
                                }
                            }
                        }
                    }

                    // Color palette accents
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        colorOptions.forEach { hex ->
                            val color = try { Color(android.graphics.Color.parseColor(hex)) } catch (_: Exception) { PinkPrimary }
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (favoriteColorHex == hex) 2.5.dp else 0.dp,
                                        color = if (favoriteColorHex == hex) DarkText else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { favoriteColorHex = hex }
                            )
                        }
                    }
                }
            }

            // Name Field
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    if (it.isNotBlank()) nameError = false
                },
                label = { Text("Celebrant Name *") },
                placeholder = { Text("e.g. Alex Rivera") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = PinkPrimary) },
                isError = nameError,
                supportingText = { if (nameError) Text("Name cannot be empty") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_name"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PinkPrimary,
                    focusedLabelColor = PinkPrimary,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                ),
                singleLine = true
            )

            // Phone Number Field (Optional for call/message)
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number (Optional)") },
                placeholder = { Text("e.g. +1 555-0192") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = PinkPrimary) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_phone"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PinkPrimary,
                    focusedLabelColor = PinkPrimary,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                ),
                singleLine = true
            )

            // Date of Birth Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCE7F3))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = PinkPrimary)
                        Text(
                            text = "Date of Birth",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                    }

                    // Month & Day Selectors
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Month Dropdown/Selector
                        Column(modifier = Modifier.weight(1.3f)) {
                            Text("Month", style = MaterialTheme.typography.labelMedium, color = SubtitleText)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { birthMonth = if (birthMonth == 1) 12 else birthMonth - 1 },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Text("◀", fontSize = 12.sp, color = DarkText)
                                }
                                Text(
                                    text = Month.of(birthMonth).name.take(3),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PinkPrimary
                                )
                                IconButton(
                                    onClick = { birthMonth = if (birthMonth == 12) 1 else birthMonth + 1 },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Text("▶", fontSize = 12.sp, color = DarkText)
                                }
                            }
                        }

                        // Day Selector
                        val maxDaysInMonth = when (birthMonth) {
                            2 -> 29
                            4, 6, 9, 11 -> 30
                            else -> 31
                        }
                        if (birthDay > maxDaysInMonth) birthDay = maxDaysInMonth

                        Column(modifier = Modifier.weight(1f)) {
                            Text("Day", style = MaterialTheme.typography.labelMedium, color = SubtitleText)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { birthDay = if (birthDay == 1) maxDaysInMonth else birthDay - 1 },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Text("◀", fontSize = 12.sp, color = DarkText)
                                }
                                Text(
                                    text = birthDay.toString(),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PinkPrimary
                                )
                                IconButton(
                                    onClick = { birthDay = if (birthDay >= maxDaysInMonth) 1 else birthDay + 1 },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Text("▶", fontSize = 12.sp, color = DarkText)
                                }
                            }
                        }
                    }

                    // Include Year Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Include Birth Year", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = DarkText)
                            Text("Calculates age automatically", style = MaterialTheme.typography.bodySmall, color = SubtitleText)
                        }
                        Switch(
                            checked = includeYear,
                            onCheckedChange = { includeYear = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PinkPrimary)
                        )
                    }

                    if (includeYear) {
                        OutlinedTextField(
                            value = birthYearText,
                            onValueChange = { if (it.length <= 4) birthYearText = it },
                            label = { Text("Birth Year") },
                            placeholder = { Text("e.g. 1998") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // Relationship Category
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Relationship Category",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    relationshipCategories.forEach { category ->
                        val isSelected = relationship.equals(category, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = { relationship = category },
                            label = { Text(category, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PinkContainer,
                                selectedLabelColor = PinkPrimary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                selectedBorderColor = PinkPrimary,
                                borderColor = Color(0xFFE2E8F0)
                            )
                        )
                    }
                }
            }

            // Reminder Lead-Time
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                    Text(
                        text = "Reminder Lead-Time",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    leadTimeOptions.forEach { (days, label) ->
                        val isSelected = reminderLeadDays == days
                        FilterChip(
                            selected = isSelected,
                            onClick = { reminderLeadDays = days },
                            label = { Text(label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFFEF3C7),
                                selectedLabelColor = Color(0xFF78350F)
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                selectedBorderColor = GoldAccent,
                                borderColor = Color(0xFFE2E8F0)
                            )
                        )
                    }
                }
            }

            // Gift Ideas
            OutlinedTextField(
                value = giftIdeas,
                onValueChange = { giftIdeas = it },
                label = { Text("Gift Ideas (Optional)") },
                placeholder = { Text("e.g. Coffee beans, art supplies, book voucher...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_gift_ideas"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PinkPrimary,
                    focusedLabelColor = PinkPrimary,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                ),
                minLines = 2,
                maxLines = 4
            )

            // Notes Field
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (Interests, likes, allergies)") },
                placeholder = { Text("e.g. Loves dark chocolate, favorite flowers are sunflowers...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_notes"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PinkPrimary,
                    focusedLabelColor = PinkPrimary,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                ),
                minLines = 2,
                maxLines = 4
            )

            // Save Button
            Button(
                onClick = {
                    if (name.isBlank()) {
                        nameError = true
                        return@Button
                    }

                    val finalYear = if (includeYear) birthYearText.toIntOrNull() else null
                    val contact = BirthdayContact(
                        id = initialContact?.id ?: 0L,
                        name = name.trim(),
                        phoneNumber = phoneNumber.trim(),
                        photoUri = photoUri,
                        birthMonth = birthMonth,
                        birthDay = birthDay,
                        birthYear = finalYear,
                        relationship = relationship,
                        reminderLeadDays = reminderLeadDays,
                        notes = notes.trim(),
                        giftIdeas = giftIdeas.trim(),
                        favoriteColorHex = favoriteColorHex,
                        avatarEmoji = avatarEmoji
                    )
                    onSave(contact)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("save_contact_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isEditing) "Save Changes" else "Add Birthday",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
