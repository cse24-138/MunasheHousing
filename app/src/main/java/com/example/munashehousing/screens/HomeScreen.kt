package com.example.munashehousing.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.example.munashehousing.R
import com.example.munashehousing.models.Property
import com.example.munashehousing.models.UserRole
import com.example.munashehousing.ui.viewmodels.PropertyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    role: UserRole,
    userEmail: String,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    initialTab: Int,
    onTabChange: (Int) -> Unit,
    onPropertyClick: (Property) -> Unit,
    onLogout: () -> Unit,
    onChatClick: (String) -> Unit,
    onReservationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onHelpClick: () -> Unit,
    onAddPropertyClick: () -> Unit,
    onFilterClick: () -> Unit,
    viewModel: PropertyViewModel
) {

    val properties by viewModel.properties.collectAsState()

    Scaffold(

        topBar = {

            if (initialTab == 0) {

                TopAppBar(

                    title = {

                        Text(
                            text = if (role == UserRole.LANDLORD)
                                stringResource(R.string.landlord_portal)
                            else
                                stringResource(R.string.app_name),

                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },

                    actions = {

                        IconButton(onClick = onFilterClick) {

                            Icon(
                                Icons.Default.FilterList,
                                contentDescription = "Filter"
                            )
                        }

                        IconButton(onClick = onToggleDarkMode) {

                            Icon(
                                imageVector = if (isDarkMode)
                                    Icons.Default.LightMode
                                else
                                    Icons.Default.DarkMode,

                                contentDescription = "Toggle Dark Mode"
                            )
                        }

                        IconButton(onClick = onLogout) {

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Logout"
                            )
                        }
                    }
                )
            }
        },

        floatingActionButton = {

            if (initialTab == 0 && role == UserRole.LANDLORD) {

                FloatingActionButton(
                    onClick = onAddPropertyClick,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {

                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Property",
                        tint = Color.White
                    )
                }
            }
        },

        bottomBar = {

            NavigationBar {

                NavigationBarItem(
                    selected = initialTab == 0,
                    onClick = { onTabChange(0) },

                    label = {
                        Text(
                            if (role == UserRole.LANDLORD)
                                "My Assets"
                            else
                                "Explore"
                        )
                    },

                    icon = {

                        Icon(
                            if (role == UserRole.LANDLORD)
                                Icons.Default.HomeWork
                            else
                                Icons.Default.Explore,
                            contentDescription = null
                        )
                    }
                )

                NavigationBarItem(
                    selected = initialTab == 1,
                    onClick = { onTabChange(1) },

                    label = {
                        Text("Messages")
                    },

                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.Message,
                            contentDescription = null
                        )
                    }
                )

                NavigationBarItem(
                    selected = initialTab == 2,
                    onClick = { onTabChange(2) },

                    label = {
                        Text("Profile")
                    },

                    icon = {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null
                        )
                    }
                )
            }
        }

    ) { paddingValues ->

        Box(
            modifier = Modifier.padding(paddingValues)
        ) {

            when (initialTab) {

                0 -> {

                    if (role == UserRole.LANDLORD) {

                        LandlordExploreSection(
                            properties,
                            onPropertyClick
                        )

                    } else {

                        ExploreSection(
                            properties,
                            onPropertyClick,
                            viewModel
                        )
                    }
                }

                1 -> {

                    MessagesSection(
                        role,
                        onChatClick
                    )
                }

                2 -> {

                    ProfileSection(
                        role,
                        userEmail,
                        onLogout,
                        onReservationsClick,
                        onSettingsClick,
                        onHelpClick
                    )
                }
            }
        }
    }
}

@Composable
fun MessagesSection(
    role: UserRole,
    onChatClick: (String) -> Unit
) {

    var searchQuery by remember {
        mutableStateOf("")
    }

    val isSearchingForStudents =
        role == UserRole.LANDLORD

    val contactList =
        if (isSearchingForStudents) {

            listOf(
                "Munashe (Student)" to "cse24-001@thuto.bac.ac.bw"
            )

        } else {

            listOf(
                "Agent Neo (Landlord)" to "neo@landlord.com"
            )
        }

    val filteredContacts =
        contactList.filter {
            it.first.contains(searchQuery, true)
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = if (isSearchingForStudents)
                "Find Students"
            else
                "Find Landlords",

            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,

            onValueChange = {
                searchQuery = it
            },

            placeholder = {
                Text("Search...")
            },

            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(24.dp),

            leadingIcon = {
                Icon(
                    Icons.Default.PersonSearch,
                    contentDescription = null
                )
            }
        )

        LazyColumn {

            items(filteredContacts) { contact ->

                ListItem(

                    headlineContent = {
                        Text(contact.first)
                    },

                    supportingContent = {
                        Text(contact.second)
                    },

                    modifier = Modifier.clickable {
                        onChatClick(contact.first)
                    }
                )
            }
        }
    }
}

@Composable
fun ProfileSection(
    role: UserRole,
    userEmail: String,
    onLogout: () -> Unit,
    onReservationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onHelpClick: () -> Unit
) {

    val context = LocalContext.current

    val sharedPreferences =
        context.getSharedPreferences(
            "profile_prefs",
            Context.MODE_PRIVATE
        )

    var profileImageUri by remember {
        mutableStateOf(
            sharedPreferences
                .getString("profile_image_uri", null)
                ?.toUri()
        )
    }

    val galleryLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->

            profileImageUri = uri

            sharedPreferences
                .edit()
                .putString(
                    "profile_image_uri",
                    uri.toString()
                )
                .apply()
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            contentAlignment = Alignment.BottomEnd
        ) {

            Surface(
                modifier = Modifier
                    .size(120.dp)
                    .clickable {
                        galleryLauncher.launch("image/*")
                    },

                shape = CircleShape,

                color = MaterialTheme.colorScheme.primaryContainer,

                tonalElevation = 4.dp
            ) {

                if (profileImageUri != null) {

                    AsyncImage(
                        model = profileImageUri,

                        contentDescription = "Profile Picture",

                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),

                        contentScale = ContentScale.Crop
                    )

                } else {

                    Icon(
                        imageVector = Icons.Default.Person,

                        contentDescription = "Default Profile",

                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),

                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.primary,
                        CircleShape
                    )
                    .padding(6.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Picture",
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (role == UserRole.LANDLORD)
                "Landlord Account"
            else
                "Student Account",

            style = MaterialTheme.typography.headlineSmall,

            fontWeight = FontWeight.Bold
        )

        Text(
            text = userEmail,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(40.dp))

        ProfileMenuItem(
            Icons.Default.Home,
            "My Reservations",
            onReservationsClick
        )

        ProfileMenuItem(
            Icons.Default.Settings,
            "Account Settings",
            onSettingsClick
        )

        ProfileMenuItem(
            Icons.AutoMirrored.Filled.Help,
            "Help & Support",
            onHelpClick
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onLogout,

            modifier = Modifier.fillMaxWidth(),

            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {

            Text("Logout")
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(vertical = 12.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Gray
        )
    }
}

@Composable
fun LandlordExploreSection(
    properties: List<Property>,
    onPropertyClick: (Property) -> Unit
) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {

        items(properties) { property ->

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable {
                        onPropertyClick(property)
                    },

                shape = RoundedCornerShape(12.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    AsyncImage(
                        model = property.imageUrl,
                        contentDescription = null,

                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(12.dp)),

                        contentScale = ContentScale.Crop
                    )

                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {

                        Text(
                            property.title,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            "P${property.price}",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExploreSection(
    properties: List<Property>,
    onPropertyClick: (Property) -> Unit,
    viewModel: PropertyViewModel
) {

    LandlordExploreSection(
        properties,
        onPropertyClick
    )
}