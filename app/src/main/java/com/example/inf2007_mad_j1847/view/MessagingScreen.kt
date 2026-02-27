package com.example.inf2007_mad_j1847.view

import android.Manifest
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
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
    val clipboardManager = LocalContext.current.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    // --- State for Scrolling ---
    val listState = rememberLazyListState()
    val showScrollToBottom by remember {
        derivedStateOf {
            // Show button if the user has scrolled up from the bottom
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 100
        }
    }

    // --- Live Location Logic ---
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var activeLiveMessageId by remember { mutableStateOf<String?>(null) }
    var expandedLiveMessage by remember { mutableStateOf<Message?>(null) }

    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val lastLocation = locationResult.lastLocation ?: return
                activeLiveMessageId?.let { messageId ->
                    // Update location and timestamp in Firestore
                    viewModel.updateLiveLocation(
                        recipientId = chatId,
                        messageId = messageId,
                        lat = lastLocation.latitude,
                        lng = lastLocation.longitude
                    )
                }
            }
        }
    }

    LaunchedEffect(chatId) {
        viewModel.listenToMessages(recipientId = chatId)
    }

    // Initial scroll to bottom and scroll on new messages
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Stop tracking if message is marked inactive in DB
    LaunchedEffect(messages) {
        val currentLiveMsg = messages.find { it.id == activeLiveMessageId }
        if (currentLiveMsg != null && !currentLiveMsg.isLive) {
            activeLiveMessageId = null
        }
    }

    // Manage Location Updates Lifecycle
    LaunchedEffect(activeLiveMessageId) {
        if (activeLiveMessageId != null) {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L)
                .setMinUpdateDistanceMeters(1f)
                .build()

            try {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
                }
            } catch (e: SecurityException) {
                scope.launch { snackbarHostState.showSnackbar("Location permission required") }
            }
        } else {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    DisposableEffect(Unit) {
        onDispose { fusedLocationClient.removeLocationUpdates(locationCallback) }
    }

    var userInput by remember { mutableStateOf("") }
    var showAttachmentOptions by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            startSharing(fusedLocationClient, viewModel, chatId, scope, snackbarHostState) { activeLiveMessageId = it }
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

    expandedLiveMessage?.let { message ->
        FullScreenMap(
            message = message,
            chatName = chatName,
            onDismiss = { expandedLiveMessage = null }
        )
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
        floatingActionButton = {
            // Scroll to Bottom Button
            AnimatedVisibility(
                visible = showScrollToBottom,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            if (messages.isNotEmpty()) {
                                listState.animateScrollToItem(messages.size - 1)
                            }
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape,
                    modifier = Modifier.padding(bottom = 60.dp).size(48.dp)
                ) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Scroll to Bottom")
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                state = listState
            ) {
                items(messages, key = { it.id }) { message ->
                    MessageBubble(
                        message = message,
                        isMe = message.senderId == currentUserId,
                        chatName = chatName,
                        onStopLive = {
                            viewModel.stopLiveLocation(chatId, message.id)
                            if (activeLiveMessageId == message.id) activeLiveMessageId = null
                        },
                        onExpandLive = { msg ->
                            expandedLiveMessage = msg
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showAttachmentOptions = true }) {
                    Icon(Icons.Default.Add, "Attach", tint = MaterialTheme.colorScheme.primary)
                }
                BasicTextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    modifier = Modifier.weight(1f).border(1.dp, Color.Gray, RoundedCornerShape(24.dp)).background(Color.White, RoundedCornerShape(24.dp)).padding(12.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    decorationBox = { innerTextField ->
                        if (userInput.isEmpty()) Text("Type a message...", color = Color.Gray)
                        innerTextField()
                    }
                )
                IconButton(onClick = {
                    if (userInput.isNotBlank()) {
                        // grab clipboard silently when user sends message
                        val clipText = clipboardManager.primaryClip?.getItemAt(0)?.text?.toString()
                        viewModel.sendMessage(chatId, userInput, clipText)
                        userInput = ""
                    }
                }) {
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
                    checkLocationAndStart(context, locationPermissionLauncher, fusedLocationClient, viewModel, chatId, scope, snackbarHostState) { activeLiveMessageId = it }
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

// --- Helper Functions ---
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
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        startSharing(client, vm, chatId, scope, state, onStarted)
    } else {
        launcher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
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
        client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener { loc ->
            if (loc != null) {
                vm.sendLiveLocationMessage(chatId, loc.latitude, loc.longitude) { onStarted(it) }
            } else {
                scope.launch { state.showSnackbar("Unable to get current location. Try moving outdoors.") }
            }
        }.addOnFailureListener { e ->
            scope.launch { state.showSnackbar("Failed to get location: ${e.message}") }
        }
    } catch (e: SecurityException) {
        scope.launch { state.showSnackbar("Location permission required") }
    }
}

@Composable
fun MessageBubble(
    message: Message,
    isMe: Boolean,
    chatName: String,
    onStopLive: () -> Unit,
    onExpandLive: (Message) -> Unit
) {
    val alignment = if (isMe) Arrangement.End else Arrangement.Start
    val bubbleColor = if (isMe) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isMe) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = alignment) {
        when (message.type) {
            MessageType.LIVE_LOCATION.name -> LiveLocationBubble(message, isMe, chatName, onStopLive, onExpandLive)
            MessageType.MEDIA.name -> MediaMessageBubble(message, isMe)
            else -> {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .background(bubbleColor, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(text = message.text, color = contentColor)
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun MediaMessageBubble(message: Message, isMe: Boolean) {
    val context = LocalContext.current
    val backgroundColor = if (isMe) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isMe) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

    val url = message.mediaUrl ?: ""
    val mime = message.mimeType ?: ""
    val fileName = message.fileName ?: "File"

    Box(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .background(backgroundColor, shape = RoundedCornerShape(12.dp))
            .padding(12.dp)
            .widthIn(min = 50.dp, max = 280.dp)
    ) {
        if (mime.startsWith("image/")) {
            // --- Image Display Logic from MessagingScreen (1) ---
            Column {
                coil.compose.AsyncImage(
                    model = url,
                    contentDescription = fileName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp, max = 220.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = fileName,
                    color = textColor,
                    fontSize = 12.sp
                )
            }
        } else {
            // --- General File Logic ---
            Column(
                modifier = Modifier.clickable {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }
            ) {
                Text(
                    text = "📎 $fileName",
                    color = textColor,
                    fontSize = 16.sp
                )
                Text(
                    text = "Tap to open",
                    color = textColor.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun LiveLocationBubble(
    message: Message,
    isMe: Boolean,
    chatName: String,
    onStopLive: () -> Unit,
    onExpandLive: (Message) -> Unit
) {
    val location = LatLng(message.latitude ?: 0.0, message.longitude ?: 0.0)
    val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
    val timeString = sdf.format(message.timestamp.toDate())

    val bubbleBackground = if (isMe) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isMe) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier
            .width(280.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bubbleBackground)
            .then(if (message.isLive) { Modifier.clickable { onExpandLive(message) }} else Modifier)
            .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    null,
                    tint = if (message.isLive) Color.Red else Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (message.isLive) "Live" else "Ended",
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
            if (message.isLive) LiveBadge()
        }
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
                        getMapAsync {
                            it.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
                            it.addMarker(MarkerOptions().position(location))
                        }
                    }
                },
                update = { view ->
                    view.getMapAsync { it.animateCamera(CameraUpdateFactory.newLatLng(location)) }
                }
            )
        }
        if (message.isLive && isMe) {
            TextButton(onClick = onStopLive, modifier = Modifier.fillMaxWidth()) {
                Text("Stop Sharing", color = Color.Red)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenMap(message: Message, chatName: String, onDismiss: () -> Unit) {
    val location = LatLng(message.latitude ?: 0.0, message.longitude ?: 0.0)
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val isMyLocation = message.senderId == currentUserId
    val title = if (isMyLocation) "My Live Location" else "$chatName Live Location"

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(top = 24.dp)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize().statusBarsPadding(),
                topBar = {
                    TopAppBar(
                        title = { Text(title) },
                        navigationIcon = {
                            IconButton(onClick = onDismiss) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                    )
                }
            ) { innerPadding ->
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            MapView(ctx).apply {
                                onCreate(Bundle())
                                onResume()
                                getMapAsync { googleMap ->
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17f))
                                    googleMap.addMarker(MarkerOptions().position(location))
                                    googleMap.uiSettings.isZoomControlsEnabled = true
                                }
                            }
                        },
                        update = { view ->
                            view.getMapAsync { it.animateCamera(CameraUpdateFactory.newLatLng(location)) }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LiveBadge() {
    val transition = rememberInfiniteTransition(label = "")
    val alpha by transition.animateFloat(
        1f,
        0.2f,
        infiniteRepeatable(tween(1000), RepeatMode.Reverse),
        label = ""
    )
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(Color.Red.copy(alpha = alpha))
    )
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
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, style = MaterialTheme.typography.bodyLarge)
    }
}