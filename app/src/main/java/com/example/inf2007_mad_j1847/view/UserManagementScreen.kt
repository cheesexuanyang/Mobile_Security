package com.example.inf2007_mad_j1847.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.inf2007_mad_j1847.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(navController: NavHostController, adminViewModel: AdminViewModel) {
    // Collecting states from the ViewModel
    val users by adminViewModel.filteredUserList.collectAsState()
    val searchQuery by adminViewModel.searchQuery.collectAsState()
    val isLoading by adminViewModel.isLoading.collectAsState()
    val selectedFilter by adminViewModel.selectedRoleFilter.collectAsState()

    // Trigger data fetch when the screen is first loaded
    LaunchedEffect(Unit) {
        adminViewModel.fetchAllUsers()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Management") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add_user") }) {
                Icon(Icons.Default.Add, contentDescription = "Add User")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // 1. Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { adminViewModel.onSearchQueryChange(it) },
                label = { Text("Search by name or email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            // 2. Filter Chips Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == "ALL",
                    onClick = { adminViewModel.onRoleFilterChange("ALL") },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = selectedFilter == "DOCTOR",
                    onClick = { adminViewModel.onRoleFilterChange("DOCTOR") },
                    label = { Text("Doctors") }
                )
                FilterChip(
                    selected = selectedFilter == "PATIENT",
                    onClick = { adminViewModel.onRoleFilterChange("PATIENT") },
                    label = { Text("Patients") }
                )
            }

            // 3. Loading Indicator
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }

            // 4. User List
            if (users.isEmpty() && !isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No users found matching your criteria.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(users, key = { it.id }) { user ->
                        ListItem(
                            headlineContent = { Text(user.name) },
                            supportingContent = { Text("${user.role} • ${user.email}") },
                            modifier = Modifier
                                .clickable {
                                    navController.navigate("user_detail/${user.id}")
                                }
                                .padding(vertical = 4.dp),
                            trailingContent = {
                                Text(
                                    text = ">",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        )
                        HorizontalDivider(thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}