package com.example.inf2007_mad_j1847.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inf2007_mad_j1847.model.AppointmentSlot
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class DoctorAppointmentItem(
    val id: String,
    val slot: AppointmentSlot
)

class DoctorAppointmentsViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _appointments = MutableStateFlow<List<DoctorAppointmentItem>>(emptyList())
    val appointments: StateFlow<List<DoctorAppointmentItem>> = _appointments.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun fetchAppointmentsForLoggedInDoctor() {
        val doctorUid = auth.currentUser?.uid.orEmpty()
        if (doctorUid.isBlank()) {
            _error.value = "Doctor not logged in."
            _appointments.value = emptyList()
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val snap = db.collection("appointments")
                    .whereEqualTo("doctorUid", doctorUid)
                    .get()
                    .await()

                val list = snap.documents.map { doc ->
                    val slot = doc.toObject(AppointmentSlot::class.java) ?: AppointmentSlot()
                    DoctorAppointmentItem(id = doc.id, slot = slot)
                }.sortedWith(compareBy({ it.slot.date }, { it.slot.timeSlot }))

                _appointments.value = list
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load appointments."
                _appointments.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }
}
