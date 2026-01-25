package com.example.inf2007_mad_j1847.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class UIMessage(val text: String, val isMe: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagingScreen(
    navController: NavController,
    chatId: String,   // Passed from navigation
    chatName: String  // Passed from navigation
) {
    var userInput by remember { mutableStateOf("") }
    // Dummy initial data
    var messages by remember { mutableStateOf(listOf(
        UIMessage("Hello!", isMe = true),
        UIMessage("Hi, this is $chatName. How can I help?", isMe = false)
    )) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(chatName) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Chat History
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                reverseLayout = false // Standard chat order
            ) {
                items(messages) { message ->
                    MessageBubble(text = message.text, isMe = message.isMe)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Input Area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (userInput.isNotBlank()) {
                                messages = messages + UIMessage(userInput, true)
                                userInput = ""
                            }
                        }
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    decorationBox = { innerTextField ->
                        Box(modifier = Modifier.fillMaxWidth()) {
                            if (userInput.isEmpty()) {
                                Text("Type a message...", color = Color.Gray, fontSize = 16.sp)
                            }
                            innerTextField()
                        }
                    }
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (userInput.isNotBlank()) {
                            messages = messages + UIMessage(userInput, true)
                            userInput = ""
                        }
                    }
                ) {
                    Text("Send")
                }
            }
        }
    }
}

// Reusing the bubble UI logic from ChatbotScreen
@Composable
fun MessageBubble(text: String, isMe: Boolean) {
    val backgroundColor = if (isMe) MaterialTheme.colorScheme.primary else Color.LightGray
    val textColor = if (isMe) Color.White else Color.Black
    val alignment = if (isMe) Arrangement.End else Arrangement.Start

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = alignment
    ) {
        Box(
            modifier = Modifier
                .background(backgroundColor, shape = RoundedCornerShape(12.dp))
                .padding(12.dp)
                .widthIn(min = 50.dp, max = 280.dp)
        ) {
            Text(
                text = text,
                color = textColor,
                fontSize = 16.sp,
                textAlign = TextAlign.Start
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}