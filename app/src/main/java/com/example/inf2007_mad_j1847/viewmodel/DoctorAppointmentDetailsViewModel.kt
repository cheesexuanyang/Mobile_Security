package com.example.inf2007_mad_j1847.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inf2007_mad_j1847.model.AppointmentSlot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DoctorAppointmentDetailsViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _appointment = MutableStateFlow<AppointmentSlot?>(null)
    val appointment: StateFlow<AppointmentSlot?> = _appointment.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _saveError = MutableStateFlow<String?>(null)
    val saveError: StateFlow<String?> = _saveError.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    fun loadAppointmentById(appointmentId: String) {
        if (appointmentId.isBlank()) {
            _error.value = "Invalid appointment ID."
            _appointment.value = null
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val doc = db.collection("appointments")
                    .document(appointmentId)
                    .get()
                    .await()

                if (!doc.exists()) {
                    _appointment.value = null
                    _error.value = "Appointment not found."
                } else {
                    _appointment.value = doc.toObject(AppointmentSlot::class.java)
                }
            } catch (e: Exception) {
                _appointment.value = null
                _error.value = e.message ?: "Failed to load appointment."
            } finally {
                _loading.value = false
            }
        }
    }

    fun saveRemark(appointmentId: String, remark: String) {
        if (appointmentId.isBlank()) {
            _saveError.value = "Invalid appointment ID."
            return
        }

        viewModelScope.launch {
            _isSaving.value = true
            _saveError.value = null
            _saveSuccess.value = false

            try {
                db.collection("appointments")
                    .document(appointmentId)
                    .update("remark", remark)
                    .await()

                // Update local state so UI reflects immediately
                _appointment.value = _appointment.value?.copy(remark = remark)
                _saveSuccess.value = true
            } catch (e: Exception) {
                _saveError.value = e.message ?: "Update failed"
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun consumeSaveSuccess() {
        _saveSuccess.value = false
    }

    fun consumeSaveError() {
        _saveError.value = null
    }
}
