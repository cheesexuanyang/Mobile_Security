package com.example.inf2007_mad_j1847.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inf2007_mad_j1847.model.User
import com.example.inf2007_mad_j1847.repo.AppointmentsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PatientBookingViewModel(
    private val repo: AppointmentsRepository = AppointmentsRepository()
) : ViewModel() {

    /* ===================== DOCTORS ===================== */

    private val _doctors = MutableStateFlow<List<User>>(emptyList())
    val doctors = _doctors.asStateFlow()

    fun loadDoctors() {
        viewModelScope.launch {
            _doctors.value = repo.fetchDoctors()
        }
    }

    /* ===================== DATE & TIME ===================== */

    private val _selectedDate = MutableStateFlow("")
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _selectedTimeSlot = MutableStateFlow("")
    val selectedTimeSlot: StateFlow<String> = _selectedTimeSlot.asStateFlow()

    private val _bookedSlots = MutableStateFlow<Set<String>>(emptySet())
    val bookedSlots: StateFlow<Set<String>> = _bookedSlots.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun setDate(date: String) {
        _selectedDate.value = date
        _selectedTimeSlot.value = ""
        _error.value = validateDateTomorrowOnwards(date)
    }

    fun setTimeSlot(slot: String) {
        _selectedTimeSlot.value = slot
    }

    fun isBooked(slot: String): Boolean =
        _bookedSlots.value.contains(slot)

    fun loadBookedSlots(doctorId: String) {
        val date = _selectedDate.value
        val err = validateDateTomorrowOnwards(date)
        if (err != null || doctorId.isBlank()) {
            _bookedSlots.value = emptySet()
            return
        }

        viewModelScope.launch {
            _bookedSlots.value = repo.loadBookedSlots(doctorId, date)
        }
    }

    /* ===================== CONFIRM ===================== */

    fun confirmBooking(
        doctorId: String,
        patientUid: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val date = _selectedDate.value
        val slot = _selectedTimeSlot.value

        if (date.isBlank() || slot.isBlank()) {
            onFailure("Please select date and time")
            return
        }

        viewModelScope.launch {
            try {
                repo.confirmBooking(doctorId, patientUid, date, slot)
                _bookedSlots.value = _bookedSlots.value + slot
                onSuccess()
            } catch (e: Exception) {
                onFailure(e.message ?: "Booking failed")
            }
        }
    }

    /* ===================== VALIDATION ===================== */

    private fun validateDateTomorrowOnwards(dateStr: String): String? {
        if (dateStr.isBlank()) return "Please select a date"

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            isLenient = false
        }
        val picked = try { sdf.parse(dateStr) } catch (_: Exception) { null }
            ?: return "Invalid date format"

        val min = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, 1)
        }.time

        return if (picked.before(min)) "Date must be tomorrow onwards" else null
    }
}
