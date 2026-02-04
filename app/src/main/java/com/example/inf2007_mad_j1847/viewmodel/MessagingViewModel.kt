package com.example.inf2007_mad_j1847.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inf2007_mad_j1847.model.Role
import com.example.inf2007_mad_j1847.model.User
import com.example.inf2007_mad_j1847.model.Message
import com.example.inf2007_mad_j1847.model.MessageType
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MessagingViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // List of available contacts (People I can message)
    private val _contacts = MutableStateFlow<List<User>>(emptyList())
    val contacts: StateFlow<List<User>> = _contacts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Holds the list of messages for the active chat
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    // 1. Helper to generate consistent Chat ID (Alphabetical Order)
    private fun getChatId(user1: String, user2: String): String {
        return if (user1 < user2) {
            "${user1}_${user2}"
        } else {
            "${user2}_${user1}"
        }
    }

    // 2. Listen to Messages (Real-time)
    fun listenToMessages(recipientId: String) {
        val currentUserId = auth.currentUser?.uid ?: return
        val chatId = getChatId(currentUserId, recipientId)

        Log.d("MessagingVM", "Listening to chat: $chatId")

        db.collection("chats").document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("MessagingVM", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val msgs = snapshot.documents.mapNotNull { doc ->
                        try {
                            val message = doc.toObject(Message::class.java)
                            if (message != null) {
                                message.id = doc.id

                                // Debug log for live location messages
                                if (message.type == MessageType.LIVE_LOCATION.name) {
                                    val rawIsLive = doc.getBoolean("isLive") ?: false
                                    Log.d("MessagingVM", "📍 Live Location Message: id=${doc.id}, " +
                                            "message.isLive=${message.isLive}, " +
                                            "raw_isLive=$rawIsLive, " +
                                            "lat=${message.latitude}, lng=${message.longitude}")
                                }
                            }
                            message
                        } catch (ex: Exception) {
                            Log.e("MessagingVM", "Error parsing message ${doc.id}", ex)
                            null
                        }
                    }
                    _messages.value = msgs
                    val liveCount = msgs.count { it.type == MessageType.LIVE_LOCATION.name && it.isLive }
                    Log.d("MessagingVM", "Loaded ${msgs.size} messages, $liveCount active live locations")
                }
            }
    }

    // 3. Send TEXT Message
    fun sendMessage(recipientId: String, text: String) {
        val currentUserId = auth.currentUser?.uid ?: return
        val chatId = getChatId(currentUserId, recipientId)

        val newMessage = Message(
            senderId = currentUserId,
            type = MessageType.TEXT.name,
            text = text,
            timestamp = Timestamp.now()
        )

        // Add message to sub-collection
        db.collection("chats").document(chatId)
            .collection("messages")
            .add(newMessage)
            .addOnSuccessListener {
                Log.d("MessagingVM", "Text message sent successfully")
            }
            .addOnFailureListener { e ->
                Log.e("MessagingVM", "Failed to send text message", e)
            }

        // Update the top-level chat document
        val chatMeta = mapOf(
            "participants" to listOf(currentUserId, recipientId),
            "lastMessage" to text,
            "timestamp" to Timestamp.now()
        )
        db.collection("chats").document(chatId).set(chatMeta)
    }

    // 4. Send MEDIA Message
    fun sendMediaMessage(
        recipientId: String,
        fileUri: Uri,
        mimeType: String?,
        fileName: String?
    ) {
        val currentUserId = auth.currentUser?.uid ?: return
        val chatId = getChatId(currentUserId, recipientId)

        viewModelScope.launch {
            try {
                val safeName = (fileName ?: "file")
                    .replace("/", "_")
                    .replace("\\", "_")

                val objectName = "${System.currentTimeMillis()}_$safeName"

                val storageRef = storage.reference
                    .child("chat_media")
                    .child(chatId)
                    .child(objectName)

                // 1) Upload
                storageRef.putFile(fileUri).await()

                // 2) Download URL
                val downloadUrl = storageRef.downloadUrl.await().toString()

                // 3) Save Firestore message
                val mediaMessage = Message(
                    senderId = currentUserId,
                    type = MessageType.MEDIA.name,
                    text = "",
                    mediaUrl = downloadUrl,
                    fileName = fileName,
                    mimeType = mimeType,
                    timestamp = Timestamp.now()
                )

                db.collection("chats").document(chatId)
                    .collection("messages")
                    .add(mediaMessage)
                    .await()

                // 4) Update chat preview
                val chatMeta = mapOf(
                    "participants" to listOf(currentUserId, recipientId),
                    "lastMessage" to "📎 Media",
                    "timestamp" to Timestamp.now()
                )
                db.collection("chats").document(chatId).set(chatMeta).await()

                Log.d("MessagingVM", "Media message sent successfully")

            } catch (e: Exception) {
                Log.e("MessagingVM", "sendMediaMessage failed", e)
            }
        }
    }

    // 5. Send Initial LIVE LOCATION Message
    fun sendLiveLocationMessage(
        recipientId: String,
        lat: Double,
        lng: Double,
        onResult: (String) -> Unit
    ) {
        val currentUserId = auth.currentUser?.uid ?: run {
            Log.e("MessagingVM", "Current user is null")
            return
        }
        val chatId = getChatId(currentUserId, recipientId)

        Log.d("MessagingVM", "✅ Sending live location: lat=$lat, lng=$lng, isLive=TRUE")

        // ✅ Use explicit HashMap to ensure proper Firestore serialization
        val liveMessage = hashMapOf<String, Any>(
            "senderId" to currentUserId,
            "type" to MessageType.LIVE_LOCATION.name,
            "text" to "",
            "latitude" to lat,
            "longitude" to lng,
            "isLive" to true,  // ⚠️ This MUST be true
            "timestamp" to Timestamp.now()
        )

        Log.d("MessagingVM", "HashMap content: $liveMessage")

        db.collection("chats").document(chatId)
            .collection("messages")
            .add(liveMessage)
            .addOnSuccessListener { docRef ->
                val messageId = docRef.id
                Log.d("MessagingVM", "✅ Message created with ID: $messageId")

                // ✅ Verify the document was written correctly
                docRef.get().addOnSuccessListener { snapshot ->
                    val storedIsLive = snapshot.getBoolean("isLive")
                    val allData = snapshot.data
                    Log.d("MessagingVM", "✅ VERIFICATION: isLive in Firestore = $storedIsLive")
                    Log.d("MessagingVM", "✅ VERIFICATION: All data = $allData")
                }.addOnFailureListener { e ->
                    Log.e("MessagingVM", "❌ Failed to verify: ${e.message}")
                }

                onResult(messageId)

                // Update chat metadata
                val chatMeta = mapOf(
                    "participants" to listOf(currentUserId, recipientId),
                    "lastMessage" to "📍 Live Location Started",
                    "timestamp" to Timestamp.now()
                )
                db.collection("chats").document(chatId).set(chatMeta)
            }
            .addOnFailureListener { e ->
                Log.e("MessagingVM", "Failed to send live location message", e)
            }
    }

    // 6. Update coordinates for an active live message
    fun updateLiveLocation(recipientId: String, messageId: String, lat: Double, lng: Double) {
        val currentUserId = auth.currentUser?.uid ?: return
        val chatId = getChatId(currentUserId, recipientId)

        val updates = hashMapOf<String, Any>(
            "latitude" to lat,
            "longitude" to lng,
            "timestamp" to com.google.firebase.Timestamp.now(),
            "isLive" to true
        )

        db.collection("chats").document(chatId)
            .collection("messages").document(messageId)
            .update(updates)
            .addOnSuccessListener {
                Log.d("MessagingVM", "Location updated: $messageId -> ($lat, $lng)")
            }
            .addOnFailureListener { e ->
                Log.e("MessagingVM", "Failed to update location for $messageId", e)
            }
    }

    // 7. Stop the live location session
    fun stopLiveLocation(recipientId: String, messageId: String) {
        val currentUserId = auth.currentUser?.uid ?: return
        val chatId = getChatId(currentUserId, recipientId)

        Log.d("MessagingVM", "🛑 Stopping live location: $messageId")

        db.collection("chats").document(chatId)
            .collection("messages").document(messageId)
            .update("isLive", false)
            .addOnSuccessListener {
                Log.d("MessagingVM", "✅ Live location stopped successfully")

                // Verify it was actually updated
                db.collection("chats").document(chatId)
                    .collection("messages").document(messageId)
                    .get()
                    .addOnSuccessListener { doc ->
                        Log.d("MessagingVM", "✅ Verification after stop: isLive = ${doc.getBoolean("isLive")}")
                    }

                // Update chat preview
                db.collection("chats").document(chatId)
                    .update("lastMessage", "📍 Live Location Ended")
            }
            .addOnFailureListener { e ->
                Log.e("MessagingVM", "❌ Failed to stop live location", e)
            }
    }

    init {
        fetchContacts()
    }

    /**
     * Fetches the list of users the current user can message.
     * - If Patient -> Fetch Doctors
     * - If Doctor -> Fetch Patients
     */
    fun fetchContacts() {
        val currentUserId = auth.currentUser?.uid ?: return
        _isLoading.value = true

        viewModelScope.launch {
            try {
                // 1. Get Current User's Role
                val userDoc = db.collection("users").document(currentUserId).get().await()
                val currentUser = userDoc.toObject(User::class.java)
                val myRole = currentUser?.role ?: Role.PATIENT

                // 2. Determine Target Role
                val targetRole = if (myRole == Role.PATIENT) Role.DOCTOR else Role.PATIENT

                // 3. Fetch Users with Target Role
                val result = db.collection("users")
                    .whereEqualTo("role", targetRole.name)
                    .get()
                    .await()

                val fetchedContacts = result.documents.mapNotNull { doc ->
                    val user = doc.toObject(User::class.java)
                    user?.copy(id = doc.id)
                }
                _contacts.value = fetchedContacts

                Log.d("MessagingVM", "Fetched ${fetchedContacts.size} contacts for role ${targetRole.name}")

            } catch (e: Exception) {
                Log.e("MessagingVM", "Error fetching contacts", e)
                _contacts.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
