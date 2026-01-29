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

    /**
     * Call this explicitly from the screen.
     * Safe to call multiple times (will just reload).
     */
    fun loadDoctors() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val db = Firebase.firestore
                val result = db.collection("users")
                    .whereEqualTo("role", Role.DOCTOR.name)
                    .get()
                    .await()

                android.util.Log.d(
                    "BookingVM",
                    "Fetched ${result.documents.size} doctor docs"
                )

                _doctors.value = result.documents.mapNotNull { doc ->
                    doc.toObject(User::class.java)?.copy(id = doc.id)
                }
            } catch (e: Exception) {
                android.util.Log.e("BookingVM", "Failed to fetch doctors", e)
                _doctors.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
