package com.example.inf2007_mad_j1847.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.util.Locale

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat

@Composable
fun TimeSlotPicker(
    selectedTimeSlot: String,
    onTimeSelected: (String) -> Unit,
    disabledSlots: Set<String> = emptySet() // ✅ NEW
) {
    val morningSlots = listOf(
        "08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00"
    )

    val afternoonSlots = listOf(
        "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Morning Slots",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(morningSlots) { timeSlot ->
                TimeSlotChip(
                    timeSlot = timeSlot,
                    isSelected = selectedTimeSlot == timeSlot,
                    isDisabled = disabledSlots.contains(timeSlot), // ✅ NEW
                    onTimeSelected = onTimeSelected
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Afternoon Slots",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(afternoonSlots) { timeSlot ->
                TimeSlotChip(
                    timeSlot = timeSlot,
                    isSelected = selectedTimeSlot == timeSlot,
                    isDisabled = disabledSlots.contains(timeSlot), // ✅ NEW
                    onTimeSelected = onTimeSelected
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSlotChip(
    timeSlot: String,
    isSelected: Boolean,
    isDisabled: Boolean,                 // ✅ NEW
    onTimeSelected: (String) -> Unit
) {
    val displayTime = convertToReadableTime(timeSlot)

    val bgColor =
        when {
            isDisabled -> MaterialTheme.colorScheme.surfaceVariant
            isSelected -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.surface
        }

    val borderColor =
        when {
            isDisabled -> MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            isSelected -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.outline
        }

    val textColor =
        when {
            isDisabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
            isSelected -> Color.White
            else -> MaterialTheme.colorScheme.onSurface
        }

    Surface(
        shape = MaterialTheme.shapes.small,
        color = bgColor,
        border = BorderStroke(width = 1.dp, color = borderColor),
        modifier = Modifier
            // ✅ Only clickable if NOT disabled
            .clickable(enabled = !isDisabled) {
                onTimeSelected(timeSlot)
            }
    ) {
        Text(
            text = displayTime,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            color = textColor
        )
    }
}

// helper function to convert 24-hour format to 12-hour format with am/pm
fun convertToReadableTime(time24h: String): String {
    val hour = time24h.substring(0, 2).toInt()
    val minute = time24h.substring(3, 5)

    val amPm = if (hour < 12) "AM" else "PM"
    val hour12 = when (hour) {
        0 -> 12
        in 1..12 -> hour
        else -> hour - 12
    }

    return "$hour12:$minute $amPm"
}

// helper function to combine date and time into a firebase Timestamp
fun createTimestamp(dateString: String, timeString: String): Timestamp? {
    if (dateString.isBlank() || timeString.isBlank()) return null

    return try {
        // Expect exact padded format: 2026-01-30 and 08:30
        val dateTimeString = "$dateString $timeString"
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).apply {
            isLenient = false
        }

        val date = sdf.parse(dateTimeString) ?: return null
        Timestamp(date) // ✅ simplest and correct
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
