package com.example.inf2007_mad_j1847

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import androidx.navigation.navArgument
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.inf2007_mad_j1847.ui.patient.booking.SelectDoctorScreen
import com.example.inf2007_mad_j1847.view.*
import com.example.inf2007_mad_j1847.view.admin.*
import com.example.inf2007_mad_j1847.view.auth.*
import com.example.inf2007_mad_j1847.view.doctor.*
import com.example.inf2007_mad_j1847.view.patient.*
import com.example.inf2007_mad_j1847.viewmodel.AdminViewModel
import com.example.inf2007_mad_j1847.viewmodel.AuthViewModel
import com.example.inf2007_mad_j1847.viewmodel.PatientAppointmentsViewModel
import com.example.inf2007_mad_j1847.viewmodel.PatientBookingViewModel


@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    val authViewModel: AuthViewModel = viewModel()
    val adminViewModel: AdminViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "auth_graph",
        modifier = modifier
    ) {
        // --- Auth Graph (Login/Signup) ---
        navigation(startDestination = "login_screen", route = "auth_graph") {
            composable("login_screen") { LoginScreen(navController, authViewModel) }
            composable("signup_screen") { SignUpScreen(navController, authViewModel) }
        }

        // --- Patient Graph ---
        navigation(startDestination = "patient_home", route = "patient_graph") {
            composable("patient_home") {
                PatientHomeScreen(navController, authViewModel)
            }

            // Booking Flow
            navigation(
                startDestination = "select_doctor",
                route = "booking_graph"
            ) {
                composable("select_doctor"){ backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry("booking_graph")
                    }
                    val vm: PatientBookingViewModel =
                        androidx.lifecycle.viewmodel.compose.viewModel(parentEntry)

                    SelectDoctorScreen(
                        navController = navController,
                        vm = vm
                    )
                }

                composable("select_time_slot") { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry("booking_graph")
                    }
                    val vm: PatientBookingViewModel =
                        androidx.lifecycle.viewmodel.compose.viewModel(parentEntry)

                    SelectTimeSlotScreen(
                        navController = navController,
                        vm = vm
                    )
                }
            }

            navigation(
                startDestination = "patient_appointments_list",
                route = "view_appointment_graph"
            ) {
                composable("patient_appointments_list") { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry("view_appointment_graph")
                    }
                    val vm: PatientAppointmentsViewModel =
                        androidx.lifecycle.viewmodel.compose.viewModel(parentEntry)

                    PatientAppointmentsScreen(
                        navController = navController,
                        vm = vm,
                        onAppointmentClick = { appointmentId ->
                            navController.navigate("patient_appointment_detail/$appointmentId")
                        }
                    )
                }

//                composable("patient_appointment_detail/{appointmentId}") { backStackEntry ->
//                    val appointmentId = backStackEntry.arguments?.getString("appointmentId")!!
//
//                    val parentEntry = remember(backStackEntry) {
//                        navController.getBackStackEntry("view_appointment_graph")
//                    }
//                    val vm: AppointmentDetailViewModel =
//                        androidx.lifecycle.viewmodel.compose.viewModel(parentEntry)
//
//                    AppointmentDetailScreen(
//                        navController = navController,
//                        vm = vm,
//                        appointmentId = appointmentId,
//                        onBack = { navController.popBackStack() }
//                    )
//                }
            }

        }

        // --- Doctor Graph ---
        navigation(startDestination = "doctor_home", route = "doctor_graph") {
            composable("doctor_home") { DoctorHomeScreen(navController, authViewModel) }
            composable("doctor_appointments") { DoctorAppointmentsScreen(navController) }
            composable("appointment_details/{appointmentId}") { backStackEntry ->
                val appointmentId = backStackEntry.arguments?.getString("appointmentId") ?: ""
                AppointmentDetailsScreen(navController, appointmentId)
            }
        }

        // --- Admin Graph ---
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

            composable("user_detail/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                UserDetailScreen(userId, navController, adminViewModel)
            }
        }

        // --- Messaging / Chat Routes (New) ---
        composable("conversation_list_screen") {
            ConversationListScreen(navController)
        }

        composable("select_contact_screen") {
            SelectContactScreen(navController)
        }

        composable(
            route = "messaging_screen/{chatId}/{chatName}",
            arguments = listOf(
                navArgument("chatId") { type = NavType.StringType },
                navArgument("chatName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            val chatName = backStackEntry.arguments?.getString("chatName") ?: "Chat"
            MessagingScreen(navController, chatId, chatName)
        }

        // --- Other Existing Routes ---
        composable("map_screen") { MapScreen(navController) }
        composable("qr_scanner_screen") { QRScannerScreen(navController) }
        composable("book_appointment_screen") { BookAppointmentScreen(navController) }
        composable("appointments") { AppointmentScreen(navController) }

        composable("profile_screen") { com.example.inf2007_mad_j1847.screens.ProfileScreen(navController) }
        composable("chatbot_screen") { ChatbotScreen(navController) }

        // Dynamic route for appointment selection after QR scan
        composable("appointment_selection/{hospitalName}") { backStackEntry ->
            val hospitalName = backStackEntry.arguments?.getString("hospitalName") ?: ""
            AppointmentSelectionScreen(navController, hospitalName)
        }
    }
}