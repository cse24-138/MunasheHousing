package com.example.munashehousing.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.munashehousing.R
import com.example.munashehousing.models.UserRole
import com.example.munashehousing.ui.viewmodels.PropertyViewModel

@Composable
fun RegisterScreen(
    viewModel: PropertyViewModel,
    onRegisterComplete: (UserRole, String) -> Unit // UPDATED: Added String for email
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var id by remember { mutableStateOf("") }
    var guardian by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.STUDENT) }

    val emailRegex = Regex("^[a-zA-Z0-9.-]+@thuto\\.bac\\.ac\\.bw$")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = stringResource(R.string.register), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = stringResource(R.string.i_am_a), style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = selectedRole == UserRole.STUDENT, onClick = { selectedRole = UserRole.STUDENT })
            Text(text = stringResource(R.string.student), modifier = Modifier.padding(end = 16.dp))

            RadioButton(selected = selectedRole == UserRole.LANDLORD, onClick = { selectedRole = UserRole.LANDLORD })
            Text(text = stringResource(R.string.landlord))
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.full_name)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(if (selectedRole == UserRole.STUDENT) stringResource(R.string.school_email) else stringResource(R.string.email)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = id,
            onValueChange = { id = it },
            label = { Text(stringResource(R.string.national_id)) },
            modifier = Modifier.fillMaxWidth()
        )

        if (selectedRole == UserRole.STUDENT) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = guardian,
                onValueChange = { guardian = it },
                label = { Text(stringResource(R.string.guardian_name)) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.password)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text(stringResource(R.string.confirm_password)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = confirmPassword.isNotEmpty() && confirmPassword != password
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val isEmailValid = if (selectedRole == UserRole.STUDENT) email.matches(emailRegex) else email.contains("@")

                when {
                    name.isBlank() || email.isBlank() || password.isBlank() || phone.isBlank() -> {
                        Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                    }
                    !isEmailValid -> {
                        val msg = if (selectedRole == UserRole.STUDENT) context.getString(R.string.school_email_invalid) else "Invalid email format"
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                    password != confirmPassword -> {
                        Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    }
                    password.length < 6 -> {
                        Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        viewModel.registerUser(name, email, phone, selectedRole, id, guardian)
                        Toast.makeText(context, context.getString(R.string.registration_successful), Toast.LENGTH_SHORT).show()
                        // FIX: Pass the role AND the email back to MainActivity
                        onRegisterComplete(selectedRole, email)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(stringResource(R.string.register))
        }
    }
}