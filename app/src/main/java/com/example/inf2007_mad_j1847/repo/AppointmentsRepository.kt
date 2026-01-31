package com.example.inf2007_mad_j1847.repo

import android.util.Log
import com.example.inf2007_mad_j1847.model.AppointmentSlot
import com.example.inf2007_mad_j1847.model.Role
import com.example.inf2007_mad_j1847.model.User
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AppointmentsRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    /* ===================== DOCTORS ===================== */

    suspend fun fetchDoctors(): List<User> {
        val result = db.collection("users")
            .whereEqualTo("role", Role.DOCTOR.name)
            .get()
            .await()

        return result.documents.mapNotNull { doc ->
            doc.toObject(User::class.java)?.copy(id = doc.id)
        }
    }

    /* ===================== Filter DOCTOR TIME SLOTS ===================== */

    suspend fun loadBookedSlots(
        doctorId: String,
        date: String
    ): Set<String> {
        val snap = db.collection("appointments")
            .whereEqualTo("doctorUid", doctorId)
            .whereEqualTo("date", date)
            .get()
            .await()

        val booked = snap.toObjects(AppointmentSlot::class.java)
            .map { it.timeSlot }
            .filter { it.isNotBlank() }
            .toSet()

        Log.d("AppointmentsRepo", "Booked slots = $booked")
        return booked
    }

    /* ===================== BOOKING ===================== */

    suspend fun confirmBooking(
        doctorId: String,
        patientUid: String,
        date: String,
        timeSlot: String
    ) {
        val safeSlot = timeSlot.replace(":", "")
        val docId = "${doctorId}_${date}_$safeSlot"
        val docRef = db.collection("appointments").document(docId)

        db.runTransaction { txn ->
            val snap = txn.get(docRef)
            if (snap.exists()) {
                val status = snap.getString("status") ?: "booked"
                if (status.lowercase() != "cancelled") {
                    throw IllegalStateException("Slot already booked")
                }
            }

            val appointment = AppointmentSlot(
                doctorUid = doctorId,
                PatientUid = patientUid,
                date = date,
                timeSlot = timeSlot,
                status = "booked",
                createdAt = Timestamp.now()
            )

            txn.set(docRef, appointment)
        }.await()
    }
}
