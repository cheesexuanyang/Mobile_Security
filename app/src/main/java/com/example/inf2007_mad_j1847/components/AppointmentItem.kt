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
import com.example.inf2007_mad_j1847.model.AppointmentStatus


@Composable
fun AppointmentItem(
    appointment: AppointmentSlot,
    onClick: () -> Unit
) {
    val isCancelled = appointment.statusEnum == AppointmentStatus.CANCELLED

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .let {
                if (!isCancelled) {
                    it.clickable { onClick() }
                } else {
                    it // no click
                }
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCancelled)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "${appointment.date} • ${appointment.timeSlot}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Status: ${appointment.status}",
                style = MaterialTheme.typography.bodySmall,
                color = if (isCancelled)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.primary
            )

            if (isCancelled) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "This appointment has been cancelled",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
