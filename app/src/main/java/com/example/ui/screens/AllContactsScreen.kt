package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.BirthdayContact
import com.example.ui.components.BirthdayCard
import com.example.ui.theme.DarkText
import com.example.ui.theme.PinkContainer
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.SoftPinkBg
import com.example.ui.theme.SubtitleText
import com.example.viewmodel.BirthdayViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllContactsScreen(
    viewModel: BirthdayViewModel,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contacts by viewModel.filteredContacts.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategoryFilter.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val today = LocalDate.now()

    val categories = listOf("All", "Family", "Friend", "Bestie", "Partner", "Work", "Other")

    // Group contacts by first uppercase letter
    val groupedContacts = remember(contacts) {
        contacts.groupBy { it.name.trim().firstOrNull()?.uppercaseChar() ?: '#' }
            .toSortedMap()
    }

    // Map letters to index in the LazyColumn for fast-scrolling
    val letterToIndexMap = remember(groupedContacts) {
        val map = mutableMapOf<Char, Int>()
        var currentIndex = 0
        groupedContacts.forEach { (letter, contactList) ->
            map[letter] = currentIndex
            currentIndex += 1 + contactList.size // 1 for header + items
        }
        map
    }

    val alphabet = ('A'..'Z').toList()

    Scaffold(
        modifier = modifier.testTag("all_contacts_screen"),
        containerColor = SoftPinkBg,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "All Contacts",
                            style = MaterialTheme.typography.headlineLarge,
                            color = DarkText,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = PinkContainer
                        ) {
                            Text(
                                text = "${contacts.size}",
                                color = PinkPrimary,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToAdd,
                        modifier = Modifier.testTag("contacts_add_button")
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = PinkPrimary,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Contact",
                                tint = Color.White,
                                modifier = Modifier.padding(6.dp)
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
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search by name, category, or notes...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PinkPrimary) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = SubtitleText)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("contacts_search_bar"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PinkPrimary,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                ),
                singleLine = true
            )

            // Relationship Filter Chips Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setCategoryFilter(category) },
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

            // Main Content Area with A-Z Letter Index Strip
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (contacts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔍", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No contacts found",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (searchQuery.isNotBlank()) "Try a different search term" else "Add your friends and family birthdays",
                                style = MaterialTheme.typography.bodySmall,
                                color = SubtitleText,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(end = 28.dp), // space for letter index
                        contentPadding = PaddingValues(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        groupedContacts.forEach { (letter, letterContacts) ->
                            item(key = "header_$letter") {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFFCE7F3),
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = letter.toString(),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PinkPrimary,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            items(
                                items = letterContacts,
                                key = { it.id }
                            ) { contact ->
                                BirthdayCard(
                                    contact = contact,
                                    onClick = { onNavigateToDetail(contact.id) },
                                    today = today
                                )
                            }
                        }
                    }
                }

                // Vertical Alphabetical Letter Index Strip on the right
                if (contacts.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 4.dp)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        alphabet.forEach { char ->
                            val hasContacts = groupedContacts.containsKey(char)
                            Text(
                                text = char.toString(),
                                fontSize = 11.sp,
                                fontWeight = if (hasContacts) FontWeight.ExtraBold else FontWeight.Normal,
                                color = if (hasContacts) PinkPrimary else Color(0xFFCBD5E1),
                                modifier = Modifier
                                    .clickable(enabled = hasContacts) {
                                        val targetIndex = letterToIndexMap[char]
                                        if (targetIndex != null) {
                                            coroutineScope.launch {
                                                listState.animateScrollToItem(targetIndex)
                                            }
                                        }
                                    }
                                    .padding(vertical = 1.dp, horizontal = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
