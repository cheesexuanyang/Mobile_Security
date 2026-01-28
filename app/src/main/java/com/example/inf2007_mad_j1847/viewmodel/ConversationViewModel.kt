package com.example.inf2007_mad_j1847.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inf2007_mad_j1847.model.User
import com.example.inf2007_mad_j1847.view.ConversationPreview
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

class ConversationViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _conversations = MutableStateFlow<List<ConversationPreview>>(emptyList())
    val conversations: StateFlow<List<ConversationPreview>> = _conversations.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        listenToConversations()
    }

    private fun listenToConversations() {
        val currentUserId = auth.currentUser?.uid ?: return
        _isLoading.value = true

        // 1. Listen to all chats where "participants" contains currentUserId
        db.collection("chats")
            .whereArrayContains("participants", currentUserId)
            // .orderBy("timestamp", Query.Direction.DESCENDING) // Requires a composite index in Firebase Console
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("ConversationVM", "Listen failed.", e)
                    _isLoading.value = false
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    viewModelScope.launch {
                        val loadedConversations = mutableListOf<ConversationPreview>()

                        for (doc in snapshot.documents) {
                            val participants = doc.get("participants") as? List<String> ?: continue
                            val lastMessage = doc.getString("lastMessage") ?: ""
                            val timestamp = doc.getTimestamp("timestamp")

                            // 2. Identify the "Other" user
                            val otherUserId = participants.find { it != currentUserId } ?: continue

                            // 3. Fetch the other user's details (Name)
                            // Optimization: In a real app, you might cache this or store name in chatMeta
                            val name = try {
                                val userDoc = db.collection("users").document(otherUserId).get().await()
                                val user = userDoc.toObject(User::class.java)
                                user?.name ?: "Unknown User"
                            } catch (e: Exception) {
                                "Unknown User"
                            }

                            // 4. Format Time
                            val formattedTime = timestamp?.toDate()?.let {
                                SimpleDateFormat("hh:mm a", Locale.getDefault()).format(it)
                            } ?: ""

                            loadedConversations.add(
                                ConversationPreview(
                                    id = otherUserId, // We use the other user's ID for navigation
                                    name = name,
                                    lastMessage = lastMessage,
                                    time = formattedTime
                                )
                            )
                        }

                        // Sort locally if index is missing (Newest first)
                        _conversations.value = loadedConversations.sortedByDescending { it.time }
                        _isLoading.value = false
                    }
                }
            }
    }
}