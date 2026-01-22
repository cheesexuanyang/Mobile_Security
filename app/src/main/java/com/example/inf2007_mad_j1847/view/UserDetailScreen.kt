package com.example.inf2007_mad_j1847.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.inf2007_mad_j1847.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(userId: String, navController: NavHostController, adminViewModel: AdminViewModel) {
    // Collect the users list and find the specific user by the passed ID
    val users by adminViewModel.filteredUserList.collectAsState()
    val user = users.find { it.id == userId }

    var showDeleteDialog by remember { mutableStateOf(false) }

    // Confirmation Dialog Logic
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to remove ${user?.name}? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        adminViewModel.removeUser(userId) {
                            showDeleteDialog = false
                            navController.popBackStack() // Go back to management list after delete
                        }
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp)
        ) {
            user?.let {
                // Profile Information Section
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Personal Info", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Name: ${it.name}", style = MaterialTheme.typography.titleLarge)
                        Text("Username: ${it.username}")
                        Text("Email: ${it.email}")

                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                        Text("System Role", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        Text(text = it.role, style = MaterialTheme.typography.bodyLarge)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Delete Action at the bottom
                Button(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Remove User Profile")
                }
            } ?: run {
                // Fallback if user is not found
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("User not found.")
                }
            }
        }
    }
}