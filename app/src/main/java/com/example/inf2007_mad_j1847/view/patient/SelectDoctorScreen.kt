package com.example.inf2007_mad_j1847.ui.patient.booking

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.inf2007_mad_j1847.model.User
import com.example.inf2007_mad_j1847.viewmodel.PatientBookingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectDoctorScreen(
    navController: NavHostController,
    vm: PatientBookingViewModel
) {
    val doctors by vm.doctors.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val errorMsg by vm.error.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadDoctors()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Doctor") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Loading doctors...")
                    }
                }

                errorMsg != null -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = errorMsg ?: "Something went wrong",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { vm.loadDoctors() }) {
                            Text("Retry")
                        }
                    }
                }

                doctors.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("No doctors available.")
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { vm.loadDoctors() }) {
                            Text("Reload")
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(doctors, key = { it.id }) { doctor ->
                            DoctorCard(
                                doctor = doctor,
                                onClick = {
                                    vm.setSelectedDoctor(doctor)
                                    navController.navigate("select_time_slot")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DoctorCard(
    doctor: User,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = doctor.name ?: "Doctor",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
