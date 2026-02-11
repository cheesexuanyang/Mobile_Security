package com.example.inf2007_mad_j1847.viewmodel

import androidx.lifecycle.ViewModel
import com.example.inf2007_mad_j1847.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.inf2007_mad_j1847.model.Role
import com.google.firebase.auth.GoogleAuthProvider
import com.example.inf2007_mad_j1847.notifications.FCMTokenManager

class AuthViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        _uiState.value = AuthUiState.Loading

        signIn(email, password,
            onSuccess = { uid -> fetchUser(uid) },
            onError = { msg -> _uiState.value = AuthUiState.Error(msg) }
        )
    }

    private fun signIn(
        email: String,
        password: String,
        onSuccess: (uid: String) -> Unit,
        onError: (message: String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid
                if (uid == null) onError("No user returned from FirebaseAuth")
                else onSuccess(uid)
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Login failed")
            }
    }

    fun signInWithGoogle(idToken: String) {
        _uiState.value = AuthUiState.Loading

        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                val user = authResult.user

                if (user != null) {
                    handleGoogleUserProvisioning(user.uid, user.email ?: "", user.displayName ?: "")
                }
            }
            .addOnFailureListener { e ->
                _uiState.value = AuthUiState.Error(e.message ?: "Google Sign-In failed")
            }
    }

    private fun handleGoogleUserProvisioning(uid: String, email: String, name: String) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // User already exists, just fetch the profile
                    fetchUser(uid)
                } else {
                    // New user from Google - provision as PATIENT by default
                    val newUser = User(
                        id = uid,
                        name = name,
                        email = email,
                        username = email.substringBefore("@"), // Generate username from email
                        role = Role.PATIENT
                    )
                    db.collection("users").document(uid).set(newUser)
                        .addOnSuccessListener { fetchUser(uid) }
                        .addOnFailureListener { _uiState.value = AuthUiState.Error("Failed to create profile") }
                }
            }
    }

    fun signUp(email: String, password: String, name: String, username: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid ?: return@addOnSuccessListener

                db.collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            // profile exists so update it with the real UID
                            val existingDocId = querySnapshot.documents[0].id
                            db.collection("users").document(existingDocId)
                                .update("id", uid) // Claim the ID
                        } else {
                            // No profile exists, create a new one as a default PATIENT
                            val newUser = User(id = uid, name = name, email = email, username = username, role = (Role.PATIENT))
                            db.collection("users").document(uid).set(newUser)
                        }
                    }
            }
    }

    fun logout() {
        FCMTokenManager.clearToken()
        auth.signOut()
        _currentUser.value = null
        _uiState.value = AuthUiState.Idle
    }

    private fun fetchUser(uid: String) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    // choose your rule here: treat missing profile as default user
                    _currentUser.value = null
                    _uiState.value = AuthUiState.Success(role = "PATIENT")
                    return@addOnSuccessListener
                }

                val user = doc.toObject(User::class.java)
                _currentUser.value = user
                _uiState.value = AuthUiState.Success(role = user?.role?.name ?: Role.PATIENT.name)
                FCMTokenManager.registerToken()
            }
            .addOnFailureListener { e ->
                _uiState.value = AuthUiState.Error(e.message ?: "Failed to fetch user profile")
            }
    }

    private fun isInputValid(email: String, password: String, name: String, username: String): Boolean {
        if (email.isBlank() || password.isBlank() || name.isBlank() || username.isBlank()) {
            _uiState.value = AuthUiState.Error("All fields are required.")
            return false
        }

        // enforce 8 char password minimum
        if (password.length < 8) {
            _uiState.value = AuthUiState.Error("Password must be at least 8 characters.")
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = AuthUiState.Error("Please enter a valid email address.")
            return false
        }

        return true
    }

    sealed class AuthUiState {
        object Idle : AuthUiState()
        object Loading : AuthUiState()
        data class Success(val role: String) : AuthUiState()
        data class Error(val message: String) : AuthUiState()
    }
}
