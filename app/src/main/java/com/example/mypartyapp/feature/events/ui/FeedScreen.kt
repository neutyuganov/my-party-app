package com.example.mypartyapp.feature.events.ui

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mypartyapp.feature.events.domain.Event
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun FeedScreen(
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = viewModel(),
    onSignOut: () -> Unit = {}
) {
    Box(modifier = modifier.fillMaxSize()) {
        when {
            viewModel.isLoading && viewModel.events.isEmpty() -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            viewModel.errorMessage != null && viewModel.events.isEmpty() -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(viewModel.errorMessage!!)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.loadFeed() }) {
                        Text("Повторить")
                    }
                }
            }
            viewModel.events.isEmpty() -> {
                Text(
                    text = "Пока нет мероприятий 🎉",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(viewModel.events, key = { it.id }) { event ->
                        EventCard(event = event)
                    }
                }
            }
        }

        TextButton(
            onClick = onSignOut,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            Text("Выйти")
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
            Spacer(modifier = Modifier.height(4.dp))
            event.address?.let {
                Text(text = it, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(
                text = formatDate(event.startsAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (event.paymentType == "free") "Бесплатно"
                           else "${event.price} ₽",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                event.maxGuests?.let {
                    Text(
                        text = "до $it чел.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

private val RU_LOCALE = Locale("ru")
private val DATE_FORMATTER = DateTimeFormatter.ofPattern("d MMMM, HH:mm", RU_LOCALE)

private fun formatDate(isoString: String): String {
    return try {
        OffsetDateTime.parse(isoString).format(DATE_FORMATTER)
    } catch (e: Exception) {
        isoString
    }
}
