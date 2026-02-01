package com.example.inf2007_mad_j1847.view.patient


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.inf2007_mad_j1847.components.BackTopBarScreen
import com.example.inf2007_mad_j1847.viewmodel.PatientAppointmentDetailViewModel


@Composable
fun PatientAppointmentDetailScreen(
    navController: NavHostController,
    vm: PatientAppointmentDetailViewModel,
    appointmentId: String,
    onCancelled: () -> Unit
) {
    val appt by vm.appointment.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()
    val cancelSuccess by vm.cancelSuccess.collectAsState()

    var showCancelDialog by remember { mutableStateOf(false) }

    LaunchedEffect(appointmentId) {
        vm.load(appointmentId)
    }

    LaunchedEffect(cancelSuccess) {
        if (cancelSuccess) {
            vm.consumeCancelSuccess()
            onCancelled()
        }
    }

    BackTopBarScreen(
        title = "Appointment Details",
        navController = navController
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            when {
                isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                error != null -> {
                    Text(text = error ?: "Unknown error")
                }

                appt == null -> {
                    Text("Appointment not found")
                }

                else -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("${appt!!.date} • ${appt!!.timeSlot}",
                            style = MaterialTheme.typography.titleMedium)

                        appt!!.doctorUid?.let {
                            Text("Doctor: $it")
                        }

                        Text("Status: ${appt!!.status}")

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { showCancelDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Cancel Appointment")
                        }
                    }
                }
            }
        }
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel appointment?") },
            text = { Text("This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showCancelDialog = false
                    vm.cancel(appointmentId)
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Keep")
                }
            }
        )
    }
}
