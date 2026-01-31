package com.example.inf2007_mad_j1847.view.doctor

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.inf2007_mad_j1847.viewmodel.DoctorAppointmentDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentDetailsScreen(
    navController: NavHostController,
    appointmentId: String,
    vm: DoctorAppointmentDetailsViewModel = viewModel()
) {
    val context = LocalContext.current

    val appt by vm.appointment.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    val isSaving by vm.isSaving.collectAsState()
    val saveError by vm.saveError.collectAsState()
    val saveSuccess by vm.saveSuccess.collectAsState()

    var remark by remember { mutableStateOf("") }

    LaunchedEffect(appointmentId) {
        vm.loadAppointmentById(appointmentId)
    }

    LaunchedEffect(appt?.remark) {
        remark = appt?.remark.orEmpty()
    }

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            Toast.makeText(context, "Remark updated", Toast.LENGTH_SHORT).show()
            vm.consumeSaveSuccess()
        }
    }

    LaunchedEffect(saveError) {
        if (!saveError.isNullOrBlank()) {
            Toast.makeText(context, saveError!!, Toast.LENGTH_LONG).show()
            vm.consumeSaveError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Appointment Details") },
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

                appt == null -> {
                    Text("Appointment not found.")
                }

                else -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "${appt!!.date} • ${appt!!.timeSlot}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(8.dp))

                            Text(
                                text = "Patient UID: ${appt!!.patientUid}",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "Status: ${appt!!.status}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = remark,
                        onValueChange = { remark = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Remark") },
                        placeholder = { Text("Write remark (optional)") },
                        minLines = 3
                    )

                    Spacer(Modifier.weight(1f))

                    Button(
                        onClick = { vm.saveRemark(appointmentId, remark) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSaving
                    ) {
                        Text(if (isSaving) "Updating..." else "Update")
                    }
                }
            }
        }
    }
}
