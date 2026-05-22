package com.example.munashehousing.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.munashehousing.R
import com.example.munashehousing.models.Property
import com.example.munashehousing.models.UserRole
import com.example.munashehousing.ui.viewmodels.PropertyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    role: UserRole,
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
                            if (role == UserRole.LANDLORD) stringResource(R.string.landlord_portal) else stringResource(R.string.app_name),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    actions = {
                        IconButton(onClick = onFilterClick) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filter")
                        }
                        IconButton(onClick = onToggleDarkMode) {
                            Icon(
                                imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Toggle Dark Mode"
                            )
                        }
                        IconButton(onClick = onLogout) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = stringResource(R.string.logout)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        floatingActionButton = {
            if (initialTab == 0 && role == UserRole.LANDLORD) {
                FloatingActionButton(onClick = onAddPropertyClick, containerColor = MaterialTheme.colorScheme.primary) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_property), tint = Color.White)
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = initialTab == 0,
                    onClick = { onTabChange(0) },
                    label = { Text(if (role == UserRole.LANDLORD) stringResource(R.string.my_properties) else stringResource(R.string.explore)) },
                    icon = { Icon(if (role == UserRole.LANDLORD) Icons.Default.HomeWork else Icons.Default.Explore, contentDescription = null) }
                )
                NavigationBarItem(
                    selected = initialTab == 1,
                    onClick = { onTabChange(1) },
                    label = { Text(stringResource(R.string.messages)) },
                    icon = { Icon(Icons.AutoMirrored.Filled.Message, contentDescription = null) }
                )
                NavigationBarItem(
                    selected = initialTab == 2,
                    onClick = { onTabChange(2) },
                    label = { Text(stringResource(R.string.profile)) },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (initialTab) {
                0 -> {
                    if (role == UserRole.LANDLORD) {
                        LandlordExploreSection(properties, onPropertyClick)
                    } else {
                        ExploreSection(properties, onPropertyClick, viewModel)
                    }
                }
                1 -> MessagesSection(onChatClick)
                2 -> ProfileSection(role, onLogout, onReservationsClick, onSettingsClick, onHelpClick)
            }
        }
    }
}

@Composable
fun LandlordExploreSection(properties: List<Property>, onPropertyClick: (Property) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text(
                stringResource(R.string.my_properties),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        items(properties.filter { !it.isReserved }) { property ->
            PropertyCard(
                property = property,
                onClick = { onPropertyClick(property) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreSection(
    properties: List<Property>, 
    onPropertyClick: (Property) -> Unit,
    viewModel: PropertyViewModel
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { 
                    searchQuery = it 
                    viewModel.updateSearch(it)
                },
                placeholder = { Text(stringResource(R.string.search_placeholder)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { 
                            searchQuery = "" 
                            viewModel.updateSearch("")
                        }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                )
            )
        }

        if (searchQuery.isEmpty()) {
            item {
                Text(
                    stringResource(R.string.featured_houses),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(properties.filter { !it.isReserved }.take(3)) { property ->
                        FeaturedPropertyCard(property, onClick = { onPropertyClick(property) })
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        item {
            Text(
                stringResource(R.string.categories),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            LazyRow(
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val categories = listOf("All", "Studio", "Apartment", "Shared Room", "Single Room")
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { 
                            selectedCategory = category 
                            viewModel.updateCategory(category)
                        },
                        label = { Text(category) }
                    )
                }
            }
        }

        item {
            Text(
                if (searchQuery.isEmpty()) stringResource(R.string.near_you) else stringResource(R.string.search_results),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
        }

        if (properties.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.HomeWork, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(stringResource(R.string.no_properties_found), color = Color.Gray)
                }
            }
        } else {
            items(properties) { property ->
                PropertyCard(
                    property = property,
                    onClick = { onPropertyClick(property) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun FeaturedPropertyCard(property: Property, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(200.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Box {
            AsyncImage(
                model = property.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 300f
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(property.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(property.location, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
            Surface(
                modifier = Modifier.padding(12.dp).align(Alignment.TopEnd),
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "P${property.price}",
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun MessagesSection(onChatClick: (String) -> Unit) {
    var studentSearchQuery by remember { mutableStateOf("") }
    
    val registeredStudents = listOf(
        "Munashe" to "cse24-001@thuto.bac.ac.bw",
        "Thabo" to "cse24-112@thuto.bac.ac.bw",
        "Sarah" to "cse24-055@thuto.bac.ac.bw",
        "Lesedi" to "cse24-088@thuto.bac.ac.bw"
    )

    val filteredStudents = if (studentSearchQuery.isEmpty()) emptyList() else {
        registeredStudents.filter { it.first.contains(studentSearchQuery, ignoreCase = true) || it.second.contains(studentSearchQuery, ignoreCase = true) }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.messages), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = studentSearchQuery,
            onValueChange = { studentSearchQuery = it },
            placeholder = { Text(stringResource(R.string.find_student)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            leadingIcon = { Icon(Icons.Default.PersonSearch, contentDescription = null) }
        )

        if (filteredStudents.isNotEmpty()) {
            Text(stringResource(R.string.suggested_students), modifier = Modifier.padding(top = 16.dp).align(Alignment.Start), style = MaterialTheme.typography.labelMedium)
            LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                items(filteredStudents) { student ->
                    ListItem(
                        headlineContent = { Text(student.first) },
                        supportingContent = { Text(student.second) },
                        leadingContent = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                        modifier = Modifier.clickable { onChatClick(student.first) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(stringResource(R.string.recent_conversations), modifier = Modifier.align(Alignment.Start), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        
        val chats = listOf(
            "Agent Neo" to "Is the Studio in Block 8 still available?",
            "Lindiwe (Landlord)" to "Yes, you can come for viewing tomorrow.",
            "Housing Admin" to "Your reservation for CBD Apartment has been received."
        )
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(chats) { chat ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .clickable { onChatClick(chat.first) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(modifier = Modifier.size(48.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(8.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(chat.first, fontWeight = FontWeight.Bold)
                        Text(chat.second, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileSection(
    role: UserRole,
    onLogout: () -> Unit,
    onReservationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onHelpClick: () -> Unit
) {
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var profileImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        profileImageUri = uri
        profileImageBitmap = null
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        profileImageBitmap = bitmap
        profileImageUri = null
    }

    var showImageSourceDialog by remember { mutableStateOf(false) }

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text(stringResource(R.string.select_profile_pic)) },
            text = { Text(stringResource(R.string.choose_source)) },
            confirmButton = {
                TextButton(onClick = { 
                    galleryLauncher.launch("image/*")
                    showImageSourceDialog = false 
                }) {
                    Text(stringResource(R.string.gallery))
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    cameraLauncher.launch(null)
                    showImageSourceDialog = false 
                }) {
                    Text(stringResource(R.string.camera))
                }
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        Box {
            Surface(
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                if (profileImageUri != null) {
                    AsyncImage(
                        model = profileImageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else if (profileImageBitmap != null) {
                    Image(
                        bitmap = profileImageBitmap!!.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            IconButton(
                onClick = { showImageSourceDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .size(36.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit_profile), tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(if (role == UserRole.LANDLORD) stringResource(R.string.landlord_account) else stringResource(R.string.student_user), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("cse24-138@thuto.bac.ac.bw", color = Color.Gray)
        
        Spacer(modifier = Modifier.height(40.dp))
        
        ProfileMenuItem(Icons.Default.Home, if (role == UserRole.LANDLORD) stringResource(R.string.house_reservations) else stringResource(R.string.my_reservations_label), onReservationsClick)
        ProfileMenuItem(Icons.Default.Settings, stringResource(R.string.account_settings_label), onSettingsClick)
        ProfileMenuItem(Icons.AutoMirrored.Filled.Help, stringResource(R.string.help_support_label), onHelpClick)
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(stringResource(R.string.logout))
        }
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
    }
}

@Composable
fun PropertyCard(property: Property, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.height(120.dp)) {
            AsyncImage(
                model = property.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight(),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp).weight(1f)) {
                Text(property.title, fontWeight = FontWeight.Bold, maxLines = 1)
                Text("📍 ${property.location}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Spacer(modifier = Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("P${property.price}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text("/mo", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Spacer(modifier = Modifier.weight(1f))
                    Surface(
                        color = if (property.isReserved) Color.LightGray else Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            if (property.isReserved) stringResource(R.string.reserved) else stringResource(R.string.available),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            color = if (property.isReserved) Color.Gray else Color(0xFF2E7D32)
                        )
                    }
                }
            }
        }
    }
}
