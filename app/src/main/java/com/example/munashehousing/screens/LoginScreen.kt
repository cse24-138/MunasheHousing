package com.example.munashehousing.screens

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import com.example.munashehousing.models.UserRole
import com.example.munashehousing.ui.viewmodels.PropertyViewModel

@Composable
fun AppLogo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(100.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF0D47A1), Color(0xFF1976D2))
                ),
                shape = RoundedCornerShape(24.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(60.dp)) {
            val width = size.width
            val height = size.height

            val roofPath = Path().apply {
                moveTo(width / 2f, 0f)
                lineTo(width, height * 0.45f)
                lineTo(0f, height * 0.45f)
                close()
            }
            drawPath(roofPath, Color.White, style = Fill)

            drawRect(
                color = Color.White,
                topLeft = androidx.compose.ui.geometry.Offset(width * 0.15f, height * 0.45f),
                size = androidx.compose.ui.geometry.Size(width * 0.7f, height * 0.55f),
                style = Fill
            )

            drawRect(
                color = Color(0xFF0D47A1),
                topLeft = androidx.compose.ui.geometry.Offset(width * 0.4f, height * 0.65f),
                size = androidx.compose.ui.geometry.Size(width * 0.2f, height * 0.35f),
                style = Fill
            )
        }
    }
}

@Composable
fun LoginScreen(
    viewModel: PropertyViewModel,
    onLoginSuccess: (UserRole, String) -> Unit, // UPDATED: Now accepts Role and Email
    onRegisterClick: () -> Unit
) {
    val context = LocalContext.current
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
                                viewModel.login(email) { user ->
                                    if (user != null && user.role == selectedRole.name) {
                                        // FIX: Pass selectedRole and the actual email
                                        onLoginSuccess(selectedRole, email)
                                    } else {
                                        // Emergency bypass for specific IDs
                                        if (email.startsWith("cse24-")) {
                                            onLoginSuccess(UserRole.STUDENT, email)
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