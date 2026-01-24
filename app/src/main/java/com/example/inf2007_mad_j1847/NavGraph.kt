package com.example.inf2007_mad_j1847

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.inf2007_mad_j1847.view.*
import com.example.inf2007_mad_j1847.viewmodel.AdminViewModel
import com.example.inf2007_mad_j1847.viewmodel.AuthViewModel
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.inf2007_mad_j1847.view.patient.PatientHomeScreen
import com.example.inf2007_mad_j1847.view.patient.SelectDoctorScreen
import com.example.inf2007_mad_j1847.view.patient.SelectTimeSlotScreen

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    val authViewModel: AuthViewModel = viewModel()
    val adminViewModel: AdminViewModel = viewModel()

    NavHost(
        navController = navController,
        //startDestination = "auth_graph",
        // debug screen bypass
        startDestination = "patient_graph",
        modifier = modifier
    ) {
        // Auth graph
        navigation(startDestination = "login_screen", route = "auth_graph") {
            composable("login_screen") { LoginScreen(navController, authViewModel) }
            composable("signup_screen") { SignUpScreen(navController, authViewModel) }
        }

        // Patient Graph
        navigation(startDestination = "patient_home", route = "patient_graph") {
            composable("patient_home") {
                PatientHomeScreen(navController, authViewModel)
            }

            /**
             * Booking Flow (as a contained flow)
             * patient_home -> booking_graph -> select_doctor -> select_time/{doctorId}
             */
            navigation(
                startDestination = "select_doctor",
                route = "booking_graph"
            ) {
                // Step 1: Select Doctor
                composable("select_doctor") {
                    SelectDoctorScreen(navController = navController)
                }

                // Step 2: Select Time (requires doctorId)
                composable(
                    route = "select_time_slot/{doctorId}",
                    arguments = listOf(navArgument("doctorId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
                    SelectTimeSlotScreen(
                        navController = navController,
                        doctorId = doctorId
                    )
                }
            }



        }

        // Doctor Graph
        composable("doctor_home") { DoctorHomeScreen(navController, authViewModel) }

        // Admin Graph
        navigation(startDestination = "admin_home", route = "admin_graph") {
            composable("admin_home") {
                AdminHomeScreen(navController, authViewModel)
            }

            composable("user_management") {
                UserManagementScreen(navController, adminViewModel)
            }

            composable("add_user") {
                AddUserScreen(navController, adminViewModel)
            }

            // Dynamic route for User Details
            composable("user_detail/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                UserDetailScreen(userId, navController, adminViewModel)
            }
        }
    }
}