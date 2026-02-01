package com.example.inf2007_mad_j1847.repo

import android.util.Log
import com.example.inf2007_mad_j1847.model.AppointmentSlot
import com.example.inf2007_mad_j1847.model.AppointmentStatus
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
        //  Query OUTSIDE transaction
        val conflictSnap = db.collection("appointments")
            .whereEqualTo("doctorUid", doctorId)
            .whereEqualTo("date", date)
            .whereEqualTo("timeSlot", timeSlot)
            .whereEqualTo("status", AppointmentStatus.BOOKED.wire)
            .get()
            .await()

        if (!conflictSnap.isEmpty) {
            throw IllegalStateException("Slot already booked")
        }

        //  Write INSIDE transaction
        db.runTransaction { txn ->
            val docRef = db.collection("appointments").document()
            val appointment = AppointmentSlot(
                appointmentId = docRef.id,
                doctorUid = doctorId,
                patientUid = patientUid,
                date = date,
                timeSlot = timeSlot,
                status = AppointmentStatus.BOOKED.wire,
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now()
            )
            txn.set(docRef, appointment)
        }.await()
    }

    // view appointment

    suspend fun fetchPatientAppointments(patientUid: String): List<AppointmentSlot> {
        val snap = db.collection("appointments")
            .whereEqualTo("patientUid", patientUid)
            .orderBy("date")      // requires index if combined with where in some cases
            .orderBy("timeSlot")  // optional but nice
            .get()
            .await()

        return snap.documents.mapNotNull { doc ->
            doc.toObject(AppointmentSlot::class.java)
                ?.copy(appointmentId = doc.id)
        }
    }


}
