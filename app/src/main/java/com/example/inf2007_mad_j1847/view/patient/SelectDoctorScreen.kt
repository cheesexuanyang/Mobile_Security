package com.example.inf2007_mad_j1847.view.patient

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.inf2007_mad_j1847.model.User
import com.example.inf2007_mad_j1847.viewmodel.BookingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectDoctorScreen(
    navController: NavHostController,
    bookingViewModel: BookingViewModel = viewModel() // Inject the ViewModel
) {
    // Observe the state from the ViewModel
    val doctors by bookingViewModel.doctors.collectAsState()
    val isLoading by bookingViewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Select a Doctor") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                // Show a loading indicator while fetching data
                CircularProgressIndicator()
            } else if (doctors.isEmpty()) {
                // Show a message if no doctors are found
                Text("No doctors are available at the moment.")
            } else {
                // Display the list of doctors
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(doctors) { doctor ->
                        DoctorCard(doctor = doctor, onDoctorSelected = { selectedDoctor ->
                            // Navigate to the next step, passing the doctor's ID
                            navController.navigate("select_time_slot/${selectedDoctor.id}")
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun DoctorCard(doctor: User, onDoctorSelected: (User) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDoctorSelected(doctor) }, // Make the whole card clickable
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = doctor.name, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(4.dp))
            // You could add more details here, like specialization
            Text(text = "Specialization: General Practice", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
