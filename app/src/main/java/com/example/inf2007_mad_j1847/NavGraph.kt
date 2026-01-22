package com.example.inf2007_mad_j1847

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.inf2007_mad_j1847.view.AdminHomeScreen
import com.example.inf2007_mad_j1847.view.DoctorHomeScreen
import com.example.inf2007_mad_j1847.view.LoginScreen
import com.example.inf2007_mad_j1847.view.PatientHomeScreen
import com.example.inf2007_mad_j1847.view.SignUpScreen
import com.example.inf2007_mad_j1847.viewmodel.AuthViewModel

/**
 * The main navigation graph that manages screen routing.
 * Uses nested graphs to handle authentication flow separately from the main app flow.
 */
@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    // Shared viewmodel instance to maintain state across screens
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "auth_graph",
        modifier = modifier
    ) {
        // Auth graph
        navigation(startDestination = "login_screen", route = "auth_graph") {
            composable("login_screen") { LoginScreen(navController, authViewModel) }
            composable("signup_screen") {
                SignUpScreen(navController, authViewModel)
            }
        }

        // Patient Graph
        navigation(startDestination = "patient_home", route = "patient_graph") {
            composable("patient_home") { PatientHomeScreen(navController, authViewModel) }
            // Include other routes for patient here
        }

        // Doctor Graph
        composable("doctor_home") { DoctorHomeScreen(navController, authViewModel) }

        // Admin Graph
        composable("admin_home") { AdminHomeScreen(navController, authViewModel) }
    }
}
