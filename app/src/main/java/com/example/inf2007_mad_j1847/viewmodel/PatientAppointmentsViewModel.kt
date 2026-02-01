package com.example.inf2007_mad_j1847.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inf2007_mad_j1847.model.AppointmentSlot
import com.example.inf2007_mad_j1847.repo.AppointmentsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PatientAppointmentsViewModel(
    private val repo: AppointmentsRepository = AppointmentsRepository()
) : ViewModel() {

    private val _appointments = MutableStateFlow<List<AppointmentSlot>>(emptyList())
    val appointments = _appointments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun load(patientUid: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _appointments.value = repo.fetchPatientAppointments(patientUid)
            } finally {
                _isLoading.value = false
            }
        }
    }
}