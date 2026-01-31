package com.example.inf2007_mad_j1847.view.doctor

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.inf2007_mad_j1847.viewmodel.DoctorAppointmentItem
import com.example.inf2007_mad_j1847.viewmodel.DoctorAppointmentsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorAppointmentsScreen(
    navController: NavHostController,
    vm: DoctorAppointmentsViewModel = viewModel()
) {
    val appointments by vm.appointments.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    LaunchedEffect(Unit) {
        vm.fetchAppointmentsForLoggedInDoctor()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Appointments") },
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
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            when {
                loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

                !error.isNullOrBlank() -> {
                    Text(text = error!!, color = MaterialTheme.colorScheme.error)
                }

                appointments.isEmpty() -> {
                    Text("No appointments found.")
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(appointments) { item ->
                            AppointmentCard(
                                item = item,
                                onClick = {
                                    navController.navigate("appointment_details/${item.id}")
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
private fun AppointmentCard(
    item: DoctorAppointmentItem,
    onClick: () -> Unit
) {
    val slot = item.slot
    val remarkPreview = slot.remark.ifBlank { "(No remark yet)" }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${slot.date} • ${slot.timeSlot}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Patient UID: ${slot.patientUid}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Status: ${slot.status}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Remark: $remarkPreview",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
