package com.example.inf2007_mad_j1847.components

import androidx.compose.runtime.Composable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.inf2007_mad_j1847.model.AppointmentSlot

@Composable
fun AppointmentItem(
    appointment: AppointmentSlot,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // Date • Time (same visual hierarchy as doctor card)
            Text(
                text = "${appointment.date} • ${appointment.timeSlot}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(6.dp))



            // Status
            Text(
                text = "Status: ${appointment.status}",
                style = MaterialTheme.typography.bodySmall,
                color = when (appointment.status) {
                    "CANCELLED" -> MaterialTheme.colorScheme.error
                    "BOOKED" -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}
