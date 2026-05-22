package com.example.munashehousing.screens

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.munashehousing.R
import com.example.munashehousing.data.database.AppDatabase
import com.example.munashehousing.data.PropertyRepository
import com.example.munashehousing.models.UserRole
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: (UserRole) -> Unit,
    onRegisterClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.STUDENT) }

    val emailRegex = Regex("^[a-zA-Z0-9.-]+@thuto\\.bac\\.ac\\.bw$")

    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = "https://images.unsplash.com/photo-1560518883-ce09059eeffa?auto=format&fit=crop&w=1200&q=80",
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.3f),
                            Color.Black.copy(alpha = 0.7f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppLogo()

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color.White.copy(alpha = 0.9f)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.login),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Role Switch
                    TabRow(
                        selectedTabIndex = if (selectedRole == UserRole.STUDENT) 0 else 1,
                        containerColor = Color.Transparent,
                        contentColor = Color(0xFF0D47A1),
                        divider = {}
                    ) {
                        Tab(
                            selected = selectedRole == UserRole.STUDENT,
                            onClick = { selectedRole = UserRole.STUDENT },
                            text = { Text(stringResource(R.string.student)) }
                        )
                        Tab(
                            selected = selectedRole == UserRole.LANDLORD,
                            onClick = { selectedRole = UserRole.LANDLORD },
                            text = { Text(stringResource(R.string.landlord)) }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(if (selectedRole == UserRole.STUDENT) stringResource(R.string.school_email) else stringResource(R.string.email)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(stringResource(R.string.password)) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            val isEmailValid = if (selectedRole == UserRole.STUDENT) email.matches(emailRegex) else email.contains("@")
                            if (isEmailValid && password.isNotBlank()) {
                                // Authenticate using Room
                                scope.launch {
                                    val db = AppDatabase.getDatabase(context)
                                    val repo = PropertyRepository(db)
                                    val user = repo.loginUser(email)
                                    if (user != null && user.role == selectedRole.name) {
                                        onLoginSuccess(selectedRole)
                                    } else {
                                        // Allow if it's the default seeded data for testing
                                        if (email.startsWith("cse24-")) {
                                            onLoginSuccess(UserRole.STUDENT)
                                        } else {
                                            Toast.makeText(context, "Invalid credentials or role", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            } else if (!isEmailValid) {
                                val msg = if (selectedRole == UserRole.STUDENT) context.getString(R.string.school_email_invalid) else "Enter valid email"
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Enter your password", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("${stringResource(R.string.login)} AS ${selectedRole.name}", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = onRegisterClick) {
                        Text(stringResource(R.string.new_here_register), color = Color(0xFF0D47A1))
                    }
                }
            }
        }
    }
}
