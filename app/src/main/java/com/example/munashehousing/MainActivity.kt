package com.example.munashehousing

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.munashehousing.data.database.AppDatabase
import com.example.munashehousing.data.PropertyRepository
import com.example.munashehousing.models.UserRole
import com.example.munashehousing.screens.*
import com.example.munashehousing.ui.theme.MunasheHousingTheme
import com.example.munashehousing.ui.viewmodels.PropertyViewModel
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isDarkMode by rememberSaveable { mutableStateOf(false) }
            MunasheHousingTheme(darkTheme = isDarkMode) {
                MunasheHousingApp(
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = { isDarkMode = !isDarkMode }
                )
            }
        }
    }
}

@Composable
fun MunasheHousingApp(
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    viewModel: PropertyViewModel = viewModel()
) {
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("munashe_housing_prefs", Context.MODE_PRIVATE)
    }

    LaunchedEffect(Unit) {
        val db = AppDatabase.getDatabase(context)
        val repo = PropertyRepository(db)
        repo.seedDatabase()
    }

    var currentScreen by rememberSaveable {
        val loggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        mutableStateOf(if (loggedIn) "home" else "welcome")
    }

    var userRole by rememberSaveable {
        val roleStr = sharedPreferences.getString("userRole", UserRole.STUDENT.name) ?: UserRole.STUDENT.name
        mutableStateOf(UserRole.valueOf(roleStr))
    }

    var userEmail by rememberSaveable {
        mutableStateOf(sharedPreferences.getString("userEmail", "Guest") ?: "Guest")
    }

    var activeTab by rememberSaveable { mutableIntStateOf(0) }
    var selectedPropertyId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedAgentName by remember { mutableStateOf("") }

    var receiptRef by rememberSaveable { mutableStateOf("") }
    var receiptAmount by rememberSaveable { mutableIntStateOf(0) }
    var receiptPlan by rememberSaveable { mutableStateOf("") }

    val properties by viewModel.properties.collectAsState()
    val selectedProperty = properties.find { it.id == selectedPropertyId }

    BackHandler(enabled = currentScreen != "home" && currentScreen != "login" && currentScreen != "welcome") {
        when (currentScreen) {
            "details" -> currentScreen = "home"
            "filter" -> currentScreen = "home"
            "lease" -> currentScreen = "details"
            "receipt" -> currentScreen = "home"
            "register" -> currentScreen = "login"
            "chat" -> { activeTab = 1; currentScreen = "home" }
            "reservations", "settings", "help", "add_property" -> { activeTab = 2; currentScreen = "home" }
        }
    }

    when (currentScreen) {
        "welcome" -> WelcomeScreen(onStart = { currentScreen = "login" })
        "login" -> LoginScreen(
            viewModel = viewModel,
            onLoginSuccess = { role, email ->
                userRole = role
                userEmail = email
                sharedPreferences.edit()
                    .putBoolean("isLoggedIn", true)
                    .putString("userRole", role.name)
                    .putString("userEmail", email)
                    .apply()
                currentScreen = "home"
            },
            onRegisterClick = { currentScreen = "register" }
        )
        "register" -> RegisterScreen(
            viewModel = viewModel,
            onRegisterComplete = { role, email ->
                userRole = role
                userEmail = email
                sharedPreferences.edit()
                    .putBoolean("isLoggedIn", true)
                    .putString("userRole", role.name)
                    .putString("userEmail", email)
                    .apply()
                currentScreen = "home"
            }
        )
        "home" -> HomeScreen(
            role = userRole,
            userEmail = userEmail,
            isDarkMode = isDarkMode,
            onToggleDarkMode = onToggleDarkMode,
            initialTab = activeTab,
            onTabChange = { activeTab = it },
            onPropertyClick = { property ->
                selectedPropertyId = property.id
                currentScreen = "details"
            },
            onLogout = {
                sharedPreferences.edit().putBoolean("isLoggedIn", false).putString("userEmail", "Guest").apply()
                currentScreen = "login"
            },
            onChatClick = { agentName ->
                selectedAgentName = agentName
                currentScreen = "chat"
            },
            onReservationsClick = { currentScreen = "reservations" },
            onSettingsClick = { currentScreen = "settings" },
            onHelpClick = { currentScreen = "help" },
            onAddPropertyClick = { currentScreen = "add_property" },
            onFilterClick = { currentScreen = "filter" },
            viewModel = viewModel
        )
        "filter" -> FilterScreen(
            currentMin = 0,
            currentMax = 10000,
            onApply = { min, max ->
                viewModel.updatePriceRange(min, max)
                viewModel.saveUserPreferences(userEmail, min, max, "")
                currentScreen = "home"
            },
            onBack = { currentScreen = "home" }
        )
        "details" -> selectedProperty?.let { property ->
            PropertyDetailsScreen(
                property = property,
                onBack = { currentScreen = "home" },
                onReserve = { currentScreen = "lease" }
            )
        }
        "lease" -> selectedProperty?.let { property ->
            LeaseAgreementScreen(
                onAgree = { isUpfront ->
                    val ref = "REF-PAY-${(1000..9999).random()}"
                    receiptRef = ref
                    receiptPlan = if (isUpfront) "Upfront (6 Months)" else "Monthly"
                    receiptAmount = if (isUpfront) (property.price * 5) + property.deposit else property.price + property.deposit
                    viewModel.reserveProperty(property)
                    currentScreen = "receipt"
                },
                onBack = { currentScreen = "details" }
            )
        }
        "receipt" -> selectedProperty?.let { property ->
            ReceiptScreen(
                property = property,
                referenceNumber = receiptRef,
                amountPaid = receiptAmount,
                paymentPlan = receiptPlan,
                onDone = { currentScreen = "home" }
            )
        }
        "chat" -> ChatScreen(
            agentName = selectedAgentName,
            viewModel = viewModel,
            userRole = userRole,
            onBack = { activeTab = 1; currentScreen = "home" }
        )
        "reservations" -> ReservationsScreen(email = userEmail, onBack = { activeTab = 2; currentScreen = "home" })
        "settings" -> SettingsScreen(email = userEmail, onBack = { activeTab = 2; currentScreen = "home" })
        "help" -> HelpSupportScreen(role = userRole, onBack = { activeTab = 2; currentScreen = "home" })
        "add_property" -> AddPropertyScreen(
            viewModel = viewModel,
            onBack = {
                activeTab = 0
                currentScreen = "home"
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationsScreen(email: String, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.my_reservations_label)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text("Reservations for: $email")
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.no_reservations))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(email: String, onBack: () -> Unit) {
    var showLanguageDialog by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.account_settings_label)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            Text("Logged in as: $email", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth().clickable { showLanguageDialog = true },
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Language, contentDescription = null)
                    Spacer(Modifier.width(16.dp))
                    Text(stringResource(R.string.language))
                }
            }
            if (showLanguageDialog) {
                AlertDialog(
                    onDismissRequest = { showLanguageDialog = false },
                    title = { Text(stringResource(R.string.select_language)) },
                    text = {
                        Column {
                            TextButton(onClick = { AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("en")); showLanguageDialog = false }) { Text(stringResource(R.string.english)) }
                            TextButton(onClick = { AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("tn")); showLanguageDialog = false }) { Text(stringResource(R.string.setswana)) }
                            TextButton(onClick = { AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("sn")); showLanguageDialog = false }) { Text(stringResource(R.string.shona)) }
                            TextButton(onClick = { AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("nd")); showLanguageDialog = false }) { Text(stringResource(R.string.ndebele)) }
                        }
                    },
                    confirmButton = { TextButton(onClick = { showLanguageDialog = false }) { Text(stringResource(R.string.close)) } }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportScreen(role: UserRole, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.help_support_label)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            val helpText = if (role == UserRole.STUDENT) "Student Support: Find a Home" else "Landlord Support: List your Property"
            Text(helpText, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.contact_support))
        }
    }
}