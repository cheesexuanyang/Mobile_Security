package com.example.inf2007_mad_j1847.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.inf2007_mad_j1847.model.Message
import com.example.inf2007_mad_j1847.model.MessageType
import com.example.inf2007_mad_j1847.viewmodel.MessagingViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagingScreen(
    navController: NavController,
    chatId: String,
    chatName: String,
    viewModel: MessagingViewModel = viewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val auth = FirebaseAuth.getInstance()
    val currentUserId = auth.currentUser?.uid ?: ""
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(chatId) {
        viewModel.listenToMessages(recipientId = chatId)
    }

    val listState = rememberLazyListState()
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    var userInput by remember { mutableStateOf("") }
    var showAttachmentOptions by remember { mutableStateOf(false) }

    // ✅ Media picker launcher (GENERAL FILES)
    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            val mimeType = context.contentResolver.getType(selectedUri)

            val fileName = run {
                val cursor = context.contentResolver.query(selectedUri, null, null, null, null)
                cursor?.use {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (it.moveToFirst() && nameIndex >= 0) it.getString(nameIndex) else null
                }
            }

            // ✅ Upload to Firebase Storage and send as MEDIA message
            viewModel.sendMediaMessage(
                recipientId = chatId,
                fileUri = selectedUri,
                mimeType = mimeType,
                fileName = fileName
            )

            scope.launch {
                snackbarHostState.showSnackbar("Media selected")
            }
        }
    }

    // Storage permission launcher (for older Android versions)
    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, launch picker
            mediaPickerLauncher.launch("*/*")
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Storage permission is required to access files")
            }
        }
    }

    // Media permission launcher (for Android 13+)
    val mediaPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, launch picker
            mediaPickerLauncher.launch("*/*")
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Media permission is required to access files")
            }
        }
    }

    // Location permission launcher (kept as your dummy behavior)
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineLocationGranted || coarseLocationGranted) {
            // Dummy location message (same as before)
            viewModel.sendMessage(recipientId = chatId, text = "📍 Location shared")
            scope.launch {
                snackbarHostState.showSnackbar("Location permission granted")
            }
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Location permission is required to share location")
            }
        }
    }

    // ✅ Function to check and request media permissions (kept in your original style)
    fun requestMediaPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED -> {
                    mediaPickerLauncher.launch("*/*")
                }
                else -> {
                    mediaPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            }
        } else {
            when {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    mediaPickerLauncher.launch("*/*")
                }
                else -> {
                    storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }
    }

    // Function to check and request location permissions (unchanged)
    fun requestLocationPermission() {
        val hasFineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        when {
            hasFineLocation || hasCoarseLocation -> {
                viewModel.sendMessage(recipientId = chatId, text = "📍 Location shared")
                scope.launch {
                    snackbarHostState.showSnackbar("Location shared")
                }
            }
            else -> {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

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
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
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
                state = listState
            ) {
                items(messages) { message ->
                    MessageBubble(
                        message = message,
                        isMe = message.senderId == currentUserId
                    )
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
                // Attachment/Clip Icon Button
                IconButton(
                    onClick = { showAttachmentOptions = true },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Attach",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Text Input Field
                BasicTextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (userInput.isNotBlank()) {
                                viewModel.sendMessage(recipientId = chatId, text = userInput)
                                userInput = ""
                            }
                        }
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, Color.Gray, RoundedCornerShape(24.dp))
                        .background(Color.White, RoundedCornerShape(24.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    decorationBox = { innerTextField ->
                        Box(modifier = Modifier.fillMaxWidth()) {
                            if (userInput.isEmpty()) {
                                Text("Type a message...", color = Color.Gray, fontSize = 16.sp)
                            }
                            innerTextField()
                        }
                    }
                )

                Spacer(modifier = Modifier.width(4.dp))

                // Send Button
                IconButton(
                    onClick = {
                        if (userInput.isNotBlank()) {
                            viewModel.sendMessage(recipientId = chatId, text = userInput)
                            userInput = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }

    // Bottom Sheet for Attachment Options
    if (showAttachmentOptions) {
        ModalBottomSheet(
            onDismissRequest = { showAttachmentOptions = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Send",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Location Option (unchanged dummy behavior)
                AttachmentOption(
                    icon = Icons.Default.LocationOn,
                    title = "Location",
                    iconBackgroundColor = Color(0xFF4CAF50),
                    onClick = {
                        showAttachmentOptions = false
                        requestLocationPermission()
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Gallery/Media Option (now general media)
                AttachmentOption(
                    icon = Icons.Default.Person,
                    title = "Gallery / Files",
                    iconBackgroundColor = Color(0xFF9C27B0),
                    onClick = {
                        showAttachmentOptions = false
                        requestMediaPermission()
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun AttachmentOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    iconBackgroundColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon with circular background
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(iconBackgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 16.sp
        )
    }
}

// ✅ Updated bubble supports TEXT + MEDIA
@Composable
fun MessageBubble(message: Message, isMe: Boolean) {
    val backgroundColor = if (isMe) MaterialTheme.colorScheme.primary else Color.LightGray
    val textColor = if (isMe) Color.White else Color.Black
    val alignment = if (isMe) Arrangement.End else Arrangement.Start
    val context = LocalContext.current

    val isMedia = message.type == MessageType.MEDIA.name && message.mediaUrl != null

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
            if (isMedia) {
                val url = message.mediaUrl!!
                val mime = message.mimeType ?: ""

                if (mime.startsWith("image/")) {
                    AsyncImage(
                        model = url,
                        contentDescription = message.fileName ?: "Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp, max = 220.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = message.fileName ?: "Image",
                        color = textColor,
                        fontSize = 12.sp
                    )
                } else {
                    Column(
                        modifier = Modifier.clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        }
                    ) {
                        Text(
                            text = "📎 ${message.fileName ?: "File"}",
                            color = textColor,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Start
                        )
                        Text(
                            text = "Tap to open",
                            color = textColor,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                Text(
                    text = message.text,
                    color = textColor,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}
