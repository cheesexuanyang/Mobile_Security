package com.example.inf2007_mad_j1847.test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun ran_warn() {
    Dialog(onDismissRequest = { }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Red),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Ransomware",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "GIVE ME A+",
                    fontSize = 56.sp,  // Bigger
                    fontWeight = FontWeight.ExtraBold,  // Bolder
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                // Optional: Add a blinking effect
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "⚠️",
                    fontSize = 32.sp,
                    color = Color.White
                )
            }
        }
    }
}