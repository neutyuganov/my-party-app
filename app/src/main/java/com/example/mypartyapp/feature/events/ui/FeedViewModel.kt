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

    var isInitialLoading by mutableStateOf(true)
        private set

    // Управляет индикатором PullToRefreshBox — устанавливается синхронно до запуска корутины,
    // чтобы два быстрых вызова не прошли проверку if (isRefreshing) одновременно.
    var isRefreshing by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadInitial()
    }

    private fun loadInitial() {
        viewModelScope.launch {
            isInitialLoading = true
            errorMessage = null
            try {
                events = repository.getFeed()
            } catch (e: Exception) {
                errorMessage = "Не удалось загрузить ленту"
            } finally {
                isInitialLoading = false
            }
        }
    }

    fun refresh() {
        if (isRefreshing) return
        isRefreshing = true
        viewModelScope.launch {
            try {
                events = repository.getFeed()
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Не удалось загрузить ленту"
            } finally {
                isRefreshing = false
            }
        }
    }
}
