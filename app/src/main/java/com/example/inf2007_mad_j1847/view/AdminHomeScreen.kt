package com.example.inf2007_mad_j1847.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.inf2007_mad_j1847.viewmodel.AuthViewModel
import androidx.compose.runtime.collectAsState

@Composable
fun AdminHomeScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    val user by authViewModel.currentUser.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Admin Dashboard", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(32.dp))

        // Profile Details Card
        user?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Welcome Back,", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Text(text = it.name, style = MaterialTheme.typography.headlineSmall)

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    Text("Username: ${it.username}", style = MaterialTheme.typography.bodyMedium)
                    Text("Email: ${it.email}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Menu Actions
        Button(
            onClick = { navController.navigate("user_management") },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Manage All Users")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = {
                authViewModel.logout()
                navController.navigate("auth_graph") {
                    popUpTo(0) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Logout")
        }
    }
}