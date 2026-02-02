package com.example.inf2007_mad_j1847.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.android.volley.Request
import com.example.inf2007_mad_j1847.model.Message
import com.example.inf2007_mad_j1847.model.MessageType
import com.example.inf2007_mad_j1847.viewmodel.MessagingViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import java.text.SimpleDateFormat
import java.util.Locale

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

    // --- Live Location Logic ---
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var activeLiveMessageId by remember { mutableStateOf<String?>(null) }

    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val lastLocation = locationResult.lastLocation ?: return
                val messageId = activeLiveMessageId
                if (messageId != null) {
                    viewModel.updateLiveLocation(
                        recipientId = chatId,
                        messageId = messageId,
                        lat = lastLocation.latitude,
                        lng = lastLocation.longitude
                    )
                    Log.d("MessagingScreen", "Location updated: ${lastLocation.latitude}, ${lastLocation.longitude}")
                }
            }
        }
    }

    LaunchedEffect(chatId) {
        viewModel.listenToMessages(recipientId = chatId)
    }

    // Stop tracking if message is marked inactive in DB
    LaunchedEffect(messages) {
        val currentLiveMsg = messages.find { it.id == activeLiveMessageId }
        if (currentLiveMsg != null && !currentLiveMsg.isLive) {
            activeLiveMessageId = null
            Log.d("MessagingScreen", "Live location stopped from DB")
        }
    }

    // Manage Location Updates Lifecycle
    LaunchedEffect(activeLiveMessageId) {
        if (activeLiveMessageId != null) {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L)
                .setMinUpdateDistanceMeters(1f)
                .build()

            try {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                ) {
                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                    )
                    Log.d("MessagingScreen", "Location updates started for message: $activeLiveMessageId")
                }
            } catch (e: SecurityException) {
                Log.e("MessagingScreen", "Location permission missing", e)
                scope.launch {
                    snackbarHostState.showSnackbar("Location permission required")
                }
            }
        } else {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            Log.d("MessagingScreen", "Location updates stopped")
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            Log.d("MessagingScreen", "Disposed location updates")
        }
    }

    val listState = rememberLazyListState()
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    var userInput by remember { mutableStateOf("") }
    var showAttachmentOptions by remember { mutableStateOf(false) }

    // Permission Launchers
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineLocationGranted || coarseLocationGranted) {
            startSharing(fusedLocationClient, viewModel, chatId, scope, snackbarHostState) { id ->
                activeLiveMessageId = id
                Log.d("MessagingScreen", "Live sharing started with ID: $id")
            }
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Location permission denied")
            }
        }
    }

    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            val fileName = context.contentResolver.query(selectedUri, null, null, null, null)?.use {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (it.moveToFirst() && index >= 0) it.getString(index) else null
            }
            viewModel.sendMediaMessage(chatId, selectedUri, context.contentResolver.getType(selectedUri), fileName)
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
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = listState
            ) {
                items(messages, key = { it.id }) { message ->
                    MessageBubble(
                        message = message,
                        isMe = message.senderId == currentUserId,
                        onStopLive = {
                            viewModel.stopLiveLocation(chatId, message.id)
                            if (activeLiveMessageId == message.id) {
                                activeLiveMessageId = null
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Input Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showAttachmentOptions = true }) {
                    Icon(Icons.Default.Add, "Attach", tint = MaterialTheme.colorScheme.primary)
                }
                BasicTextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, Color.Gray, RoundedCornerShape(24.dp))
                        .background(Color.White, RoundedCornerShape(24.dp))
                        .padding(12.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    decorationBox = { innerTextField ->
                        if (userInput.isEmpty()) {
                            Text("Type a message...", color = Color.Gray)
                        }
                        innerTextField()
                    }
                )
                IconButton(
                    onClick = {
                        if (userInput.isNotBlank()) {
                            viewModel.sendMessage(chatId, userInput)
                            userInput = ""
                        }
                    }
                ) {
                    Icon(Icons.Default.Send, "Send", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }

    if (showAttachmentOptions) {
        ModalBottomSheet(onDismissRequest = { showAttachmentOptions = false }) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                AttachmentOption(Icons.Default.LocationOn, "Live Location", Color(0xFF4CAF50)) {
                    showAttachmentOptions = false
                    checkLocationAndStart(
                        context,
                        locationPermissionLauncher,
                        fusedLocationClient,
                        viewModel,
                        chatId,
                        scope,
                        snackbarHostState
                    ) { id ->
                        activeLiveMessageId = id
                    }
                }
                AttachmentOption(Icons.Default.Person, "Gallery / Files", Color(0xFF9C27B0)) {
                    showAttachmentOptions = false
                    mediaPickerLauncher.launch("*/*")
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message, isMe: Boolean, onStopLive: () -> Unit) {
    val alignment = if (isMe) Arrangement.End else Arrangement.Start
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = alignment
    ) {
        when (message.type) {
            MessageType.LIVE_LOCATION.name -> {
                LiveLocationBubble(message, isMe, onStopLive)
            }
            MessageType.MEDIA.name -> {
                MediaMessageBubble(message, isMe)
            }
            else -> {
                // TEXT message
                val bubbleColor = if (isMe) MaterialTheme.colorScheme.primary else Color.LightGray
                Box(
                    modifier = Modifier
                        .background(bubbleColor, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = message.text,
                        color = if (isMe) Color.White else Color.Black
                    )
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun MediaMessageBubble(message: Message, isMe: Boolean) {
    val bubbleColor = if (isMe) MaterialTheme.colorScheme.primary else Color.LightGray
    Column(
        modifier = Modifier
            .widthIn(max = 260.dp)
            .background(bubbleColor, RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        message.mediaUrl?.let { url ->
            when {
                message.mimeType?.startsWith("image/") == true -> {
                    // Display image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        // Use Coil or another image loading library
                        Text("Image: ${message.fileName ?: "Untitled"}", color = Color.White)
                    }
                }
                else -> {
                    // Display file info
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "File",
                            tint = if (isMe) Color.White else Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = message.fileName ?: "File",
                            color = if (isMe) Color.White else Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun formatFirestoreTimestamp(timestamp: com.google.firebase.Timestamp): String {
    val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
    return sdf.format(timestamp.toDate())
}
@Composable
fun LiveLocationBubble(message: Message, isMe: Boolean, onStopLive: () -> Unit) {
    val location = LatLng(message.latitude ?: 0.0, message.longitude ?: 0.0)
    val timeString = formatFirestoreTimestamp(message.timestamp)

    // Define colors based on ownership
    val bubbleBackground = if (isMe) MaterialTheme.colorScheme.primaryContainer else Color(0xFFF0F0F0)
    val contentColor = if (isMe) MaterialTheme.colorScheme.onPrimaryContainer else Color.Black

    Column(
        modifier = Modifier
            .width(280.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bubbleBackground)
            .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
    ) {
        // --- Header Section ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = if (message.isLive) Color.Red else Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (message.isLive) "Live Location" else "Last known location",
                    style = MaterialTheme.typography.labelMedium,
                    color = contentColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Updated at $timeString",
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.7f)
                )
            }

            if (message.isLive) {
                LiveBadge() // Pulsing dot component
            }
        }

        // --- Map Section ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(horizontal = 4.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            AndroidView(
                factory = { ctx ->
                    MapView(ctx).apply {
                        onCreate(Bundle())
                        onResume()
                        getMapAsync { map ->
                            map.uiSettings.isMapToolbarEnabled = false
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
                            map.addMarker(MarkerOptions().position(location))
                        }
                    }
                },
                update = { mapView ->
                    mapView.getMapAsync { it.animateCamera(CameraUpdateFactory.newLatLng(location)) }
                }
            )
        }

        // --- Action Section ---
        if (message.isLive) {
            if (isMe) {
                TextButton(
                    onClick = onStopLive,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Stop Sharing", style = MaterialTheme.typography.labelLarge)
                }
            } else {
                Text(
                    "Currently sharing...",
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        } else {
            Text(
                "Sharing ended",
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun LiveBadge() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ), label = "alpha"
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(Color.Red.copy(alpha = alpha))
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            "LIVE",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Red
        )
    }
}

private fun checkLocationAndStart(
    context: Context,
    launcher: androidx.activity.result.ActivityResultLauncher<Array<String>>,
    client: FusedLocationProviderClient,
    vm: MessagingViewModel,
    chatId: String,
    scope: kotlinx.coroutines.CoroutineScope,
    state: SnackbarHostState,
    onStarted: (String) -> Unit
) {
    val hasFineLocation = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val hasCoarseLocation = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    if (hasFineLocation || hasCoarseLocation) {
        startSharing(client, vm, chatId, scope, state, onStarted)
    } else {
        launcher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}

private fun startSharing(
    client: FusedLocationProviderClient,
    vm: MessagingViewModel,
    chatId: String,
    scope: kotlinx.coroutines.CoroutineScope,
    state: SnackbarHostState,
    onStarted: (String) -> Unit
) {
    try {
        client.lastLocation.addOnSuccessListener { loc ->
            if (loc != null) {
                Log.d("MessagingScreen", "Starting live location: ${loc.latitude}, ${loc.longitude}")
                vm.sendLiveLocationMessage(chatId, loc.latitude, loc.longitude) { newId ->
                    onStarted(newId)
                    scope.launch {
                        state.showSnackbar("Live sharing started")
                    }
                }
            } else {
                scope.launch {
                    state.showSnackbar("Unable to get current location")
                }
                Log.w("MessagingScreen", "Location was null")
            }
        }.addOnFailureListener { e ->
            Log.e("MessagingScreen", "Failed to get location", e)
            scope.launch {
                state.showSnackbar("Failed to get location: ${e.message}")
            }
        }
    } catch (e: SecurityException) {
        Log.e("MessagingScreen", "Location permission missing", e)
        scope.launch {
            state.showSnackbar("Location permission required")
        }
    }
}

@Composable
fun AttachmentOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                title,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, style = MaterialTheme.typography.bodyLarge)
    }
}
