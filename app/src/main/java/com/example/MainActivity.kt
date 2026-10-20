package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Cake
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.BirthdayContact
import com.example.ui.screens.AddEditBirthdayScreen
import com.example.ui.screens.AllContactsScreen
import com.example.ui.screens.CalendarViewScreen
import com.example.ui.screens.ContactDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.CelebrateTheme
import com.example.ui.theme.DarkText
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.PinkContainer
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.SoftPinkBg
import com.example.ui.theme.SubtitleText
import com.example.viewmodel.BirthdayViewModel
import java.time.LocalDate

sealed class Screen {
    object Home : Screen()
    object Calendar : Screen()
    object Contacts : Screen()
    object Settings : Screen()
    data class ContactDetail(val contactId: Long) : Screen()
    data class AddEditContact(
        val contactId: Long? = null,
        val initialMonth: Int? = null,
        val initialDay: Int? = null
    ) : Screen()
}

enum class NavigationTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    HOME("Home", Icons.Filled.Cake, Icons.Outlined.Cake, "nav_home"),
    CALENDAR("Calendar", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth, "nav_calendar"),
    CONTACTS("Contacts", Icons.Filled.People, Icons.Outlined.People, "nav_contacts"),
    SETTINGS("Settings", Icons.Filled.Settings, Icons.Outlined.Settings, "nav_settings")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CelebrateTheme {
                CelebrateApp()
            }
        }
    }
}

@Composable
fun CelebrateApp(viewModel: BirthdayViewModel = viewModel()) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    var currentTab by remember { mutableStateOf(NavigationTab.HOME) }
    var previousScreen by remember { mutableStateOf<Screen>(Screen.Home) }

    val allContacts by viewModel.allContacts.collectAsStateWithLifecycle()
    val defaultLeadDays by viewModel.defaultReminderLeadDays.collectAsStateWithLifecycle()
    val greetingTemplate by viewModel.greetingTemplate.collectAsStateWithLifecycle()

    val showBottomBar = currentScreen is Screen.Home ||
            currentScreen is Screen.Calendar ||
            currentScreen is Screen.Contacts ||
            currentScreen is Screen.Settings

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = SoftPinkBg,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("bottom_nav_bar")
                ) {
                    NavigationTab.entries.forEach { tab ->
                        val isSelected = currentTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                currentTab = tab
                                currentScreen = when (tab) {
                                    NavigationTab.HOME -> Screen.Home
                                    NavigationTab.CALENDAR -> Screen.Calendar
                                    NavigationTab.CONTACTS -> Screen.Contacts
                                    NavigationTab.SETTINGS -> Screen.Settings
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.title
                                )
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PinkPrimary,
                                selectedTextColor = PinkPrimary,
                                unselectedIconColor = SubtitleText,
                                unselectedTextColor = SubtitleText,
                                indicatorColor = PinkContainer
                            ),
                            modifier = Modifier.testTag(tab.testTag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    is Screen.Home -> {
                        HomeScreen(
                            viewModel = viewModel,
                            onNavigateToAdd = {
                                previousScreen = Screen.Home
                                currentScreen = Screen.AddEditContact()
                            },
                            onNavigateToDetail = { id ->
                                previousScreen = Screen.Home
                                currentScreen = Screen.ContactDetail(id)
                            },
                            onNavigateToContacts = {
                                currentTab = NavigationTab.CONTACTS
                                currentScreen = Screen.Contacts
                            }
                        )
                    }

                    is Screen.Calendar -> {
                        BackHandler {
                            currentTab = NavigationTab.HOME
                            currentScreen = Screen.Home
                        }
                        CalendarViewScreen(
                            viewModel = viewModel,
                            onNavigateToDetail = { id ->
                                previousScreen = Screen.Calendar
                                currentScreen = Screen.ContactDetail(id)
                            },
                            onNavigateToAddWithDate = { month, day ->
                                previousScreen = Screen.Calendar
                                currentScreen = Screen.AddEditContact(initialMonth = month, initialDay = day)
                            }
                        )
                    }

                    is Screen.Contacts -> {
                        BackHandler {
                            currentTab = NavigationTab.HOME
                            currentScreen = Screen.Home
                        }
                        AllContactsScreen(
                            viewModel = viewModel,
                            onNavigateToDetail = { id ->
                                previousScreen = Screen.Contacts
                                currentScreen = Screen.ContactDetail(id)
                            },
                            onNavigateToAdd = {
                                previousScreen = Screen.Contacts
                                currentScreen = Screen.AddEditContact()
                            }
                        )
                    }

                    is Screen.Settings -> {
                        BackHandler {
                            currentTab = NavigationTab.HOME
                            currentScreen = Screen.Home
                        }
                        SettingsScreen(viewModel = viewModel)
                    }

                    is Screen.ContactDetail -> {
                        val contact = allContacts.find { it.id == screen.contactId }
                        ContactDetailScreen(
                            contact = contact,
                            onBack = { currentScreen = previousScreen },
                            onEdit = { c ->
                                currentScreen = Screen.AddEditContact(contactId = c.id)
                            },
                            onDelete = { c ->
                                viewModel.deleteContact(c) {
                                    currentScreen = previousScreen
                                }
                            },
                            onUpdateGiftIdeas = { c, updatedIdeas ->
                                viewModel.saveContact(c.copy(giftIdeas = updatedIdeas))
                            },
                            greetingTemplate = greetingTemplate
                        )
                    }

                    is Screen.AddEditContact -> {
                        val initialContact = screen.contactId?.let { id ->
                            allContacts.find { it.id == id }
                        } ?: BirthdayContact(
                            name = "",
                            birthMonth = screen.initialMonth ?: LocalDate.now().monthValue,
                            birthDay = screen.initialDay ?: LocalDate.now().dayOfMonth,
                            reminderLeadDays = defaultLeadDays
                        )

                        AddEditBirthdayScreen(
                            initialContact = if (screen.contactId != null) initialContact else null,
                            defaultLeadDays = defaultLeadDays,
                            onSave = { savedContact ->
                                viewModel.saveContact(savedContact) {
                                    currentScreen = previousScreen
                                }
                            },
                            onDelete = { c ->
                                viewModel.deleteContact(c) {
                                    currentScreen = previousScreen
                                }
                            },
                            onBack = { currentScreen = previousScreen }
                        )
                    }
                }
            }
        }
    }
}
