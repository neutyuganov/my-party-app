package com.example.mypartyapp.feature.events.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mypartyapp.feature.events.domain.Event
import com.example.mypartyapp.feature.events.domain.PaymentType
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

// Все возможные состояния экрана ленты — единственный источник для Crossfade
private enum class FeedUiState { Loading, Error, Empty, Content }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = viewModel()
) {
    // Переводим флаги ViewModel в одно состояние экрана
    val uiState = when {
        viewModel.isInitialLoading -> FeedUiState.Loading
        viewModel.errorMessage != null && viewModel.events.isEmpty() -> FeedUiState.Error
        viewModel.events.isEmpty() -> FeedUiState.Empty
        else -> FeedUiState.Content
    }

    // PullToRefreshBox всегда в дереве, чтобы pull-to-refresh работал в любом состоянии
    PullToRefreshBox(
        modifier = modifier.fillMaxSize(),
        isRefreshing = viewModel.isRefreshing,
        onRefresh = { viewModel.refresh() }
    ) {
        // Crossfade плавно анимирует переход между состояниями (загрузка → контент → ошибка)
        Crossfade(targetState = uiState, label = "feed_state") { state ->
            when (state) {
                FeedUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                FeedUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(viewModel.errorMessage ?: "")
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { viewModel.refresh() }) {
                                Text("Повторить")
                            }
                        }
                    }
                }

                FeedUiState.Empty -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Пока нет мероприятий 🎉")
                    }
                }

                FeedUiState.Content -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(viewModel.events, key = { it.id }) { event ->
                            EventCard(event = event)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventCard(event: Event) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            event.address?.let {
                Text(text = it, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(4.dp))
            }
            Text(
                text = formatDate(event.startsAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (event.paymentType == PaymentType.FREE) "Бесплатно" else "${event.price} ₽",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                event.maxGuests?.let {
                    Text(text = "до $it чел.", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

// Форматтеры создаются один раз на весь жизненный цикл файла — не при каждой рекомпозиции
private val RU_LOCALE = Locale.forLanguageTag("ru")
private val DATE_FORMATTER = DateTimeFormatter.ofPattern("d MMMM, HH:mm", RU_LOCALE)

private fun formatDate(isoString: String): String {
    return try {
        OffsetDateTime.parse(isoString).format(DATE_FORMATTER)
    } catch (e: Exception) {
        isoString // если строка не в ISO-формате — возвращаем как есть
    }
}
