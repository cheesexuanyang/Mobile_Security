package com.example.inf2007_mad_j1847.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.inf2007_mad_j1847.viewmodel.MessagingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectContactScreen(
    navController: NavController,
    viewModel: MessagingViewModel = viewModel() // Inject ViewModel
) {
    // Observe real data from Firestore
    val contacts by viewModel.contacts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Trigger fetch on load (already in init, but safe to call)
    LaunchedEffect(Unit) {
        viewModel.fetchContacts()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Conversation") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (contacts.isEmpty()) {
                Text(
                    text = "No contacts found.",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(contacts) { user ->
                        ListItem(
                            leadingContent = {
                                Icon(Icons.Default.Person, contentDescription = null)
                            },
                            headlineContent = { Text(user.name) }, // Real Name
                            supportingContent = { Text(user.role.displayName) }, // Real Role
                            modifier = Modifier.clickable {
                                // Navigate to Chat with Real ID and Name
                                navController.navigate("messaging_screen/${user.id}/${user.name}") {
                                    // Remove this selection screen from backstack so back button goes to list
                                    popUpTo("conversation_list_screen")
                                }
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}