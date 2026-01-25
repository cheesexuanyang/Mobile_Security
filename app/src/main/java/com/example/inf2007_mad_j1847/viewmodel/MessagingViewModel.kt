package com.example.inf2007_mad_j1847.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inf2007_mad_j1847.model.Role
import com.example.inf2007_mad_j1847.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MessagingViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // List of available contacts (People I can message)
    private val _contacts = MutableStateFlow<List<User>>(emptyList())
    val contacts: StateFlow<List<User>> = _contacts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchContacts()
    }

    /**
     * Fetches the list of users the current user can message.
     * - If Patient -> Fetch Doctors
     * - If Doctor -> Fetch Patients
     */
    fun fetchContacts() {
        val currentUserId = auth.currentUser?.uid ?: return
        _isLoading.value = true

        viewModelScope.launch {
            try {
                // 1. Get Current User's Role
                val userDoc = db.collection("users").document(currentUserId).get().await()
                val currentUser = userDoc.toObject(User::class.java)
                val myRole = currentUser?.role ?: Role.PATIENT // Default to Patient if unknown

                // 2. Determine Target Role (Opposite of mine)
                val targetRole = if (myRole == Role.PATIENT) Role.DOCTOR else Role.PATIENT

                // 3. Fetch Users with Target Role
                val result = db.collection("users")
                    .whereEqualTo("role", targetRole.name) // Query by Enum name string
                    .get()
                    .await()

                val fetchedContacts = result.toObjects(User::class.java)
                _contacts.value = fetchedContacts

                Log.d("MessagingVM", "Fetched ${fetchedContacts.size} contacts for role ${targetRole.name}")

            } catch (e: Exception) {
                Log.e("MessagingVM", "Error fetching contacts", e)
                _contacts.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}