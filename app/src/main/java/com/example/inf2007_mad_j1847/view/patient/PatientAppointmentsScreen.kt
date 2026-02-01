package com.example.inf2007_mad_j1847.view.patient

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.inf2007_mad_j1847.components.AppointmentItem
import com.example.inf2007_mad_j1847.components.BackTopBarScreen
import com.example.inf2007_mad_j1847.viewmodel.AuthViewModel
import com.example.inf2007_mad_j1847.viewmodel.PatientAppointmentsViewModel
import com.google.firebase.auth.FirebaseAuth


@Composable
fun PatientAppointmentsScreen(
    navController: NavHostController,
    vm: PatientAppointmentsViewModel,
    authViewModel: AuthViewModel,
    onAppointmentClick: (String) -> Unit
) {
    val appointments by vm.appointments.collectAsState()
    val isLoading by vm.isLoading.collectAsState()

    val user by authViewModel.currentUser.collectAsState()
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    Log.d("PatientAppointmentsScreen", "check uid: $uid")
    LaunchedEffect(uid) {
        Log.d("PatientAppointmentsScreen", "Fetching appointments for user: $uid")
        if (!uid.isNullOrBlank()) {
            vm.load(uid)
        } else {
            navController.navigate("patient_home") {
                popUpTo("patient_appointments_list") { inclusive = true }
            }
        }
    }

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
