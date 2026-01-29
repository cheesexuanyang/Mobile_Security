package com.example.inf2007_mad_j1847.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.google.firebase.Timestamp

class TimeSlotViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    // --- UI state ---
    private val _selectedDate = MutableStateFlow("")
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _dateError = MutableStateFlow<String?>(null)
    val dateError: StateFlow<String?> = _dateError.asStateFlow()

    private val _selectedTimeSlot = MutableStateFlow("")
    val selectedTimeSlot: StateFlow<String> = _selectedTimeSlot.asStateFlow()

    private val _bookedSlots = MutableStateFlow<Set<String>>(emptySet())
    val bookedSlots: StateFlow<Set<String>> = _bookedSlots.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // --- setters ---
    fun setDate(date: String) {
        _selectedDate.value = date
        _selectedTimeSlot.value = "" // clear time when date changes
        _error.value = null
        _dateError.value = validateDateTomorrowOnwards(date)
    }

    fun setTimeSlot(slot: String) {
        _selectedTimeSlot.value = slot
        _error.value = null
    }

    fun isBooked(slot: String): Boolean = _bookedSlots.value.contains(slot)

    // --- load availability ---
    fun loadBookedSlots(doctorId: String) {
        val date = _selectedDate.value
        val err = validateDateTomorrowOnwards(date)
        _dateError.value = err

        if (doctorId.isBlank() || date.isBlank() || err != null) {
            _bookedSlots.value = emptySet()
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val snap = db.collection("appointment")
                    .whereEqualTo("doctorId", doctorId)
                    .whereEqualTo("date", date)
                    // If you have status and want to ignore cancelled, uncomment:
                    // .whereIn("status", listOf("booked", "upcoming"))
                    .get()
                    .await()

                val booked = snap.documents.mapNotNull { it.getString("timeSlot") }.toSet()
                _bookedSlots.value = booked

                // If current selection became booked, clear it
                if (_selectedTimeSlot.value.isNotBlank() && booked.contains(_selectedTimeSlot.value)) {
                    _selectedTimeSlot.value = ""
                }

                Log.d("TimeSlotVM", "Booked slots for $doctorId on $date = $booked")
            } catch (e: Exception) {
                Log.e("TimeSlotVM", "Failed to load booked slots", e)
                _error.value = e.message ?: "Failed to load availability"
                _bookedSlots.value = emptySet()
            } finally {
                _loading.value = false
            }
        }
    }

    // --- validation ---
    private fun validateDateTomorrowOnwards(dateStr: String): String? {
        if (dateStr.isBlank()) return "Please select a date"

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { isLenient = false }
        val picked = try { sdf.parse(dateStr) } catch (_: Exception) { null }
            ?: return "Invalid date format (yyyy-MM-dd)"

        val min = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.DAY_OF_MONTH, 1) // tomorrow
        }.time

        return if (picked.before(min)) "Date must be tomorrow onwards" else null
    }



    /**
     * Confirm booking for current selectedDate + selectedTimeSlot
     * - Creates 1 doc per doctorId+date+timeSlot
     * - Uses transaction to avoid double booking
     */
    fun confirmBooking(
        doctorId: String,
        patientUid: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        Log.d("TimeSlotVM", "confirmBooking called")

        Log.d(
            "TimeSlotVM",
            "doctorId=$doctorId, date=${_selectedDate.value}, time=${_selectedTimeSlot.value}"
        )
        val date = _selectedDate.value
        val timeSlot = _selectedTimeSlot.value

        // basic guards
        if (doctorId.isBlank()) return onFailure("doctorId missing")
        if (patientUid.isBlank()) return onFailure("User not logged in")
        if (date.isBlank()) return onFailure("Please select a date")
        if (timeSlot.isBlank()) return onFailure("Please select a time slot")
        if (_bookedSlots.value.contains(timeSlot)) return onFailure("Slot already booked")

        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            val docId = "${doctorId}_${date}_${timeSlot}"
            val docRef = db.collection("appointments").document(docId)

            try {
                db.runTransaction { txn ->
                    val snapshot = txn.get(docRef)

                    if (snapshot.exists()) {
                        val status = snapshot.getString("status") ?: "booked"
                        // If you allow re-booking cancelled slots:
                        if (status.lowercase() != "cancelled") {
                            throw IllegalStateException("Slot already booked")
                        }
                    }

                    val data = hashMapOf(
                        "doctorId" to doctorId,
                        "uid" to patientUid,
                        "date" to date,           // yyyy-MM-dd
                        "timeSlot" to timeSlot,   // HH:mm
                        "createdAt" to Timestamp.now()
                    )

                    txn.set(docRef, data)
                }.await()

                // update local state immediately
                _bookedSlots.value = _bookedSlots.value + timeSlot
                onSuccess()
            } catch (e: Exception) {
                val msg = e.message ?: "Failed to book"
                _error.value = msg
                onFailure(msg)
            } finally {
                _loading.value = false
            }

            Log.d("TimeSlotVM", "confirmBooking end")
        }
    }




}
