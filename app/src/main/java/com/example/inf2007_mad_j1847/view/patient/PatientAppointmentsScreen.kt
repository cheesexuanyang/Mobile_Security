package com.example.inf2007_mad_j1847.view.patient

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.inf2007_mad_j1847.components.AppointmentItem
import com.example.inf2007_mad_j1847.components.BackTopBarScreen
import com.example.inf2007_mad_j1847.viewmodel.PatientAppointmentsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientAppointmentsScreen(
    navController: NavHostController,
    vm: PatientAppointmentsViewModel,
    onAppointmentClick: (String) -> Unit
) {
    val appointments by vm.appointments.collectAsState()
    val isLoading by vm.isLoading.collectAsState()

    BackTopBarScreen(
        title = "My Appointments",
        navController = navController
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                appointments.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No appointments found")
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = appointments,
                            key = { appt -> appt.appointmentId } // stable key
                        ) { appt ->
                            AppointmentItem(
                                appointment = appt,
                                onClick = { onAppointmentClick(appt.appointmentId) }
                            )
                        }
                    }
                }
            }
        }
    }
}
