package com.example.inf2007_mad_j1847.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectContactScreen(navController: NavController) {
    // Dummy Data: In reality, fetch "All Doctors" or "All Patients" from Firebase
    val contacts = listOf(
        "Dr. Smith", "Dr. Lee", "Dr. Alex", "Dr. Tan"
    )

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
        LazyColumn(
            modifier = Modifier.padding(innerPadding)
        ) {
            items(contacts) { name ->
                ListItem(
                    headlineContent = { Text(name) },
                    modifier = Modifier.clickable {
                        // Start chat with this person
                        // Use a dummy ID "999" for now
                        navController.navigate("messaging_screen/999/$name") {
                            // Pop the selection screen so back button goes to ConversationList
                            popUpTo("conversation_list_screen")
                        }
                    }
                )
                HorizontalDivider()
            }
        }
    }
}