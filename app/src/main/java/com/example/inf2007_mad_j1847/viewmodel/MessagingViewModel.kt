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

    // ✅ Firebase Storage for media uploads
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
    // This ensures UserA->UserB and UserB->UserA open the SAME chat room.
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

        // Listen to the specific chat document's "messages" sub-collection
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
                        doc.toObject(Message::class.java)?.copy(id = doc.id)
                    }
                    _messages.value = msgs
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

        // Optional: Update the top-level chat document (useful for "Last Message" previews later)
        val chatMeta = mapOf(
            "participants" to listOf(currentUserId, recipientId),
            "lastMessage" to text,
            "timestamp" to Timestamp.now()
        )
        db.collection("chats").document(chatId).set(chatMeta)
    }

    // ✅ 4. Send MEDIA Message (uploads to Firebase Storage, stores URL in Firestore)
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

            } catch (e: Exception) {
                Log.e("MessagingVM", "sendMediaMessage failed", e)
            }
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
                val myRole = currentUser?.role ?: Role.PATIENT // Default to Patient if unknown

                // 2. Determine Target Role (Opposite of mine)
                val targetRole = if (myRole == Role.PATIENT) Role.DOCTOR else Role.PATIENT

                // 3. Fetch Users with Target Role
                val result = db.collection("users")
                    .whereEqualTo("role", targetRole.name) // Query by Enum name string
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
