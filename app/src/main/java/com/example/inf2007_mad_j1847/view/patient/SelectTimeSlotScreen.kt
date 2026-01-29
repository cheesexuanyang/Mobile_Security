package com.example.inf2007_mad_j1847.view

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.inf2007_mad_j1847.components.TimeSlotPicker
import com.example.inf2007_mad_j1847.viewmodel.TimeSlotViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar
import java.util.Locale
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectTimeSlotScreen(
    navController: NavController,
    doctorId: String,
    viewModel: TimeSlotViewModel = viewModel()
) {
    val context = LocalContext.current

    val selectedDate by viewModel.selectedDate.collectAsState()
    val dateError by viewModel.dateError.collectAsState()
    val selectedTimeSlot by viewModel.selectedTimeSlot.collectAsState()
    val bookedSlots by viewModel.bookedSlots.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    // load booked slots whenever doctorId + date changes and date is valid
    LaunchedEffect(doctorId, selectedDate, dateError) {
        if (doctorId.isNotBlank() && selectedDate.isNotBlank() && dateError == null) {
            viewModel.loadBookedSlots(doctorId)
        }
    }

    fun openDatePicker() {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        val minDateMillis = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, 1)
        }.timeInMillis

        DatePickerDialog(
            context,
            { _, y, m, d ->
                val formatted = String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d)
                viewModel.setDate(formatted) // ✅ update via VM (also clears time)
            },
            year, month, day
        ).apply {
            datePicker.minDate = minDateMillis
        }.show()
    }

    val canConfirm =
        selectedDate.isNotBlank() &&
                dateError == null &&
                selectedTimeSlot.isNotBlank() &&
                !bookedSlots.contains(selectedTimeSlot) &&
                !loading

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Time Slot") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("Doctor ID: $doctorId", style = MaterialTheme.typography.bodySmall)

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = selectedDate,
                onValueChange = { },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                label = { Text("Appointment Date (yyyy-MM-dd)") },
                isError = dateError != null,
                supportingText = {
                    if (dateError != null) Text(dateError!!)
                    else Text("Tomorrow onwards only")
                },
                trailingIcon = {
                    TextButton(onClick = { openDatePicker() }) { Text("Pick") }
                }
            )

            Spacer(Modifier.height(12.dp))

            if (loading) {
                Row {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Loading availability…")
                }
                Spacer(Modifier.height(12.dp))
            }

            if (!error.isNullOrBlank()) {
                Text(error!!, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(12.dp))
            }

            if (selectedDate.isNotBlank() && dateError == null) {
                Text("Select Appointment Time", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(8.dp))

                TimeSlotPicker(
                    selectedTimeSlot = selectedTimeSlot,
                    onTimeSelected = { viewModel.setTimeSlot(it) },
                    disabledSlots = bookedSlots
                )

                if (bookedSlots.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Booked slots: ${bookedSlots.sorted().joinToString()}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            } else {
                Spacer(Modifier.height(8.dp))
                Text("Pick a valid date first to see time slots.")
            }

            Spacer(Modifier.height(24.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = canConfirm,
                onClick = {
                    // next step: write booking doc (doctorId + selectedDate + selectedTimeSlot)
                    viewModel.confirmBooking(
                        doctorId = doctorId,
                        patientUid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty(),
                        onSuccess = { Toast.makeText(
                            context,
                            "✅ Appointment booked successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                            navController.navigate("patient_home") {
                                popUpTo("patient_home") { inclusive = false }
                            }
                                    },
                        onFailure = { err ->
                            Toast.makeText(
                                context,
                                "❌ $err",
                                Toast.LENGTH_LONG
                            ).show()}
                    )
                }
            ) {
                Text("Confirm")
            }
        }
    }
}
