package com.example.inf2007_mad_j1847.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.inf2007_mad_j1847.viewmodel.AuthViewModel

@Composable
fun AdminHomeScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    val user by authViewModel.currentUser.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Admin System Control", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(20.dp))

        user?.let {
            Text("Admin User: ${it.username}", color = MaterialTheme.colorScheme.primary)
            // Add Admin-specific features: e.g., User management, Logs, Database stats
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = {
            authViewModel.logout() // Clear the session first
            navController.navigate("auth_graph") {
                popUpTo(0) { inclusive = true } // Clear the entire backstack
            }
        }) {
            Text("Logout")
        }
    }
}