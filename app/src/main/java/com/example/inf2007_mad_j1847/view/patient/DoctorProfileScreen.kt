package com.example.inf2007_mad_j1847.view.patient

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.inf2007_mad_j1847.viewmodel.PatientBookingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorProfileScreen(
    navController: NavHostController,
    vm: PatientBookingViewModel
) {
    val doctor by vm.selectedDoctor.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Doctor Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        val d = doctor
        if (d == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Text("No doctor selected.")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Dr. <name>
            Text(
                text = if (d.name.isBlank()) "Doctor" else "Dr. ${d.name}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            // Specialisation
            Text("Specialization", fontWeight = FontWeight.SemiBold)
            Text(d.specialization ?: "General Practice")

            // Bio
            Text("About", fontWeight = FontWeight.SemiBold)
            Text(d.bio ?: "No bio available.")

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { navController.navigate("select_time_slot") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue to Time Slot")
            }
        }
    }
}
