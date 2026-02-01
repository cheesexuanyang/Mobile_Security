package com.example.inf2007_mad_j1847.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inf2007_mad_j1847.model.AppointmentSlot
import com.example.inf2007_mad_j1847.repo.AppointmentsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PatientAppointmentDetailViewModel(
    private val repo: AppointmentsRepository = AppointmentsRepository()
) : ViewModel() {

    private val _appointment = MutableStateFlow<AppointmentSlot?>(null)
    val appointment = _appointment.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _cancelSuccess = MutableStateFlow(false)
    val cancelSuccess = _cancelSuccess.asStateFlow()

    fun load(appointmentId: String) {
        if (appointmentId.isBlank()) {
            _error.value = "Invalid appointment ID"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _appointment.value = repo.fetchAppointmentById(appointmentId)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load appointment"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cancel(appointmentId: String) {
        if (appointmentId.isBlank()) {
            _error.value = "Invalid appointment ID"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _cancelSuccess.value = false
            try {
                repo.cancelAppointment(appointmentId)
                _cancelSuccess.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to cancel appointment"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun consumeCancelSuccess() {
        _cancelSuccess.value = false
    }
}
