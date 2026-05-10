package com.example.mypartyapp.feature.events.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypartyapp.feature.events.data.EventRepository
import com.example.mypartyapp.feature.events.domain.Event
import kotlinx.coroutines.launch

class FeedViewModel : ViewModel() {

    private val repository = EventRepository()

    var events by mutableStateOf<List<Event>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadFeed()
    }

    fun loadFeed() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                events = repository.getFeed()
            } catch (e: Exception) {
                errorMessage = "Не удалось загрузить ленту"
            } finally {
                isLoading = false
            }
        }
    }
}
