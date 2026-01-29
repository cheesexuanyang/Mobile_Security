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
import androidx.navigation.NavController
import com.example.inf2007_mad_j1847.components.TimeSlotPicker
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectTimeSlotScreen(
    navController: NavController,
    doctorId: String
) {
    val context = LocalContext.current

    var selectedDate by remember { mutableStateOf("") }     // yyyy-MM-dd
    var selectedTimeSlot by remember { mutableStateOf("") } // HH:mm from TimeSlotPicker

    fun openDatePicker() {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        // tomorrow as min selectable date
        val minDateMillis = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, 1)
        }.timeInMillis

        DatePickerDialog(
            context,
            { _, y, m, d ->
                val formatted = String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d)
                selectedDate = formatted
                selectedTimeSlot = "" // clear time when date changes
            },
            year, month, day
        ).apply {
            datePicker.minDate = minDateMillis
        }.show()
    }

    val canConfirm = selectedDate.isNotBlank() && selectedTimeSlot.isNotBlank()

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

            // ---- DATE SELECTOR ----
            OutlinedTextField(
                value = selectedDate,
                onValueChange = { /* read-only */ },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                label = { Text("Appointment Date (yyyy-MM-dd)") },
                trailingIcon = {
                    TextButton(onClick = { openDatePicker() }) {
                        Text("Pick")
                    }
                }
            )

            Spacer(Modifier.height(16.dp))

            // ---- TIME SLOT PICKER ----
            if (selectedDate.isNotBlank()) {
                Text("Select Appointment Time", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(8.dp))

                TimeSlotPicker(
                    selectedTimeSlot = selectedTimeSlot,
                    onTimeSelected = { selectedTimeSlot = it }
                )
            } else {
                Text("Pick a date first to see time slots.")
            }

            Spacer(Modifier.height(24.dp))

            // ---- CONFIRM ----
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = canConfirm,
                onClick = {
                    // For now: just return to previous screen or navigate next
                    // Later: create appointment record using doctorId + selectedDate + selectedTimeSlot
                    navController.popBackStack()
                }
            ) {
                Text("Confirm")
            }
        }
    }
}
