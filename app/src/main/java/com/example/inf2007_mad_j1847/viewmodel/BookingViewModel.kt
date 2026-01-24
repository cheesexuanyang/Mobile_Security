package com.example.inf2007_mad_j1847.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inf2007_mad_j1847.model.Role
import com.example.inf2007_mad_j1847.model.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class BookingViewModel : ViewModel() {

    private val _doctors = MutableStateFlow<List<User>>(emptyList())
    val doctors = _doctors.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        fetchDoctors()
    }

    private fun fetchDoctors() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val db = Firebase.firestore
                val result = db.collection("users")
                    // This is the key Firestore query!
                    //.whereEqualTo("role", Role.DOCTOR.name) // Use .name to get the String "DOCTOR"
                    .get()
                    .await()

                android.util.Log.d(
                    "BookingVM",
                    "Fetched ${result.documents.size} doctor docs"
                )

                // Convert the Firestore documents into User objects
                _doctors.value = result.toObjects(User::class.java)
            } catch (e: Exception) {
                // Handle errors, e.g., log them or show a message
                _doctors.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
