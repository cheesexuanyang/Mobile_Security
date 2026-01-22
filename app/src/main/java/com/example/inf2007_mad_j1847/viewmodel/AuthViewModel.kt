package com.example.inf2007_mad_j1847.viewmodel

import androidx.lifecycle.ViewModel
import com.example.inf2007_mad_j1847.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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

    fun signUp(email: String, password: String, name: String, username: String) {
        if (!isInputValid(email, password, name, username)) return  // run the validation check first

        _uiState.value = AuthUiState.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid
                if (uid != null) {
                    val newUser = User(
                        id = uid,
                        name = name,
                        username = username,
                        email = email,
                        role = "PATIENT"
                    )

                    db.collection("users").document(uid).set(newUser)
                        .addOnSuccessListener {
                            _currentUser.value = newUser
                            _uiState.value = AuthUiState.Success(role = "PATIENT")
                        }
                        .addOnFailureListener { e ->
                            _uiState.value = AuthUiState.Error("Auth created, but profile failed: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                _uiState.value = AuthUiState.Error(e.message ?: "Sign up failed")
            }
    }

    fun logout() {
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
                _uiState.value = AuthUiState.Success(role = user?.role ?: "PATIENT")
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
