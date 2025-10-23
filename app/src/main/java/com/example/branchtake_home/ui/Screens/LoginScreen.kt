package com.example.branchtake_home.ui.Screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.branchtake_home.ViewModel.AuthViewModel
import com.example.branchtake_home.ViewModel.UiState

@Composable
fun LoginScreen(
    vm: AuthViewModel,
    onSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val state by vm.loginState.collectAsState()

    LaunchedEffect(state) {
        if (state is UiState.Success) onSuccess()
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Branch Support", style = MaterialTheme.typography.headlineMedium)
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, singleLine = true)
            OutlinedTextField(
                value = password, onValueChange = { password = it },
                label = { Text("Password (your email reversed)") },
                singleLine = true, visualTransformation = PasswordVisualTransformation()
            )

            Button(
                onClick = { vm.login(email.trim(), password.trim()) },
                enabled = state !is UiState.Loading
            ) { Text(if (state is UiState.Loading) "Signing in..." else "Sign in") }

            if (state is UiState.Error) {
                Text((state as UiState.Error).message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
