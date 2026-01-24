package com.example.inf2007_mad_j1847.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.inf2007_mad_j1847.viewmodel.AdminViewModel
import com.example.inf2007_mad_j1847.model.Role

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserScreen(navController: NavHostController, adminViewModel: AdminViewModel) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(Role.PATIENT) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New User") },
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
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Select User Role", style = MaterialTheme.typography.titleMedium)

            // Role Selection Logic
            Row(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedRole == Role.PATIENT,
                        onClick = { selectedRole = Role.PATIENT}
                    )
                    Text("Patient")
                }
                Spacer(modifier = Modifier.width(20.dp))
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedRole == Role.DOCTOR,
                        onClick = { selectedRole = Role.DOCTOR }
                    )
                    Text("Doctor")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (name.isNotEmpty() && email.isNotEmpty()) {
                        adminViewModel.addUserProfile(name, email, username, selectedRole) {
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create Placeholder Profile")
            }

            Text(
                text = "Note: This creates a Firestore profile. The user must still register via the sign-up page to set their password.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}