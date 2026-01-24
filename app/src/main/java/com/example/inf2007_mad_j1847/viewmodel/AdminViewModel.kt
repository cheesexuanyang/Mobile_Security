package com.example.inf2007_mad_j1847.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inf2007_mad_j1847.model.User
import com.example.inf2007_mad_j1847.model.Role
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*

class AdminViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _userList = MutableStateFlow<List<User>>(emptyList())
    val isLoading = MutableStateFlow(false)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedRoleFilter = MutableStateFlow("ALL")
    val selectedRoleFilter: StateFlow<String> = _selectedRoleFilter

    // Use stateIn to convert the Flow to a StateFlow correctly for the ViewModel
    val filteredUserList: StateFlow<List<User>> = combine(
        _userList, _searchQuery, _selectedRoleFilter
    ) { users, query, role ->
        users.filter { user ->
            val matchesRole = role == "ALL" || user.role.name == role
            val matchesQuery = user.name.contains(query, ignoreCase = true) ||
                    user.email.contains(query, ignoreCase = true)
            matchesRole && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onRoleFilterChange(newRole: String) {
        _selectedRoleFilter.value = newRole
    }

    fun fetchAllUsers() {
        isLoading.value = true
        db.collection("users").get()
            .addOnSuccessListener { result ->
                val users = result.documents.mapNotNull { document ->
                    val user = document.toObject(User::class.java)
                    user?.copy(id = document.id)
                }
                _userList.value = users
                isLoading.value = false
            }
            .addOnFailureListener { isLoading.value = false }
    }

    fun addUserProfile(name: String, email: String, username: String, role: Role, onSuccess: () -> Unit) {
        val newUser = User(name = name, email = email, username = username, role = role)
        db.collection("users").add(newUser)
            .addOnSuccessListener { onSuccess() }
    }

    fun removeUser(userId: String, onSuccess: () -> Unit) {
        db.collection("users").document(userId).delete()
            .addOnSuccessListener {
                fetchAllUsers()
                onSuccess()
            }
    }
}