package com.example.inf2007_mad_j1847.view.doctor

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.inf2007_mad_j1847.view.HomeScreenButton
import com.example.inf2007_mad_j1847.viewmodel.AuthViewModel

@Composable
fun DoctorHomeScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val user by authViewModel.currentUser.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Doctor Dashboard",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Welcome Back,",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                // If your doctor user object has name/email, it will show here
                Text(
                    text = user?.name ?: "Doctor",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                user?.email?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            HomeScreenButton(
                title = "Appointments",
                icon = Icons.Default.DateRange,
                onClick = {
                    // change route name if yours is different
                    navController.navigate("doctor_appointments")
                }
            )

            HomeScreenButton(
                title = "Messages",
                icon = Icons.Default.Email,
                onClick = {
                    // change route name if yours is different
                    navController.navigate("conversation_list_screen")
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = {
                authViewModel.logout()
                navController.navigate("auth_graph") {
                    popUpTo(0) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Logout")
        }
    }
}
