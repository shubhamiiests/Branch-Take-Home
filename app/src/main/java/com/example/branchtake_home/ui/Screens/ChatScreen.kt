package com.example.branchtake_home.ui.Screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.Data.Remote.MessageDto
import com.example.branchtake_home.ViewModel.ChatViewModel
import com.example.branchtake_home.ViewModel.UiState
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatScreen(
    threadId: Long,
    userId: Long,
    vm: ChatViewModel,
    onBack: () -> Unit
) {
    val state by vm.state.collectAsState()

    LaunchedEffect(threadId) { vm.load(threadId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thread $threadId • User $userId") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            when (val s = state) {
                is UiState.Loading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }

                is UiState.Error -> Text("Error: ${s.message}", Modifier.padding(16.dp))

                // No wildcard generic here; no cast needed
                is UiState.Success -> {
                    val msgs: List<MessageDto> = s.data
                    MessagesList(
                        list = msgs,
                        modifier = Modifier.weight(1f) // keep input bar at bottom
                    )
                    MessageInput(onSend = { text ->
                        if (text.isNotBlank()) vm.send(threadId, text)
                    })
                }

                else -> {}
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MessagesList(
    list: List<MessageDto>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(list) { m ->
            val isAgent = m.agentId != null
            Surface(
                tonalElevation = if (isAgent) 6.dp else 1.dp,
                shape = MaterialTheme.shapes.large
            ) {
                Column(Modifier.fillMaxWidth().padding(12.dp)) {
                    Text(
                        if (isAgent) "Agent ${m.agentId}" else "User ${m.userId}",
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(m.body)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        formatTs(m.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageInput(onSend: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    val canSend = text.isNotBlank()

    Row(
        Modifier.fillMaxWidth().padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.weight(1f),
            singleLine = false,
            maxLines = 4,
            label = { Text("Type a reply") }
        )
        Spacer(Modifier.width(8.dp))
        Button(
            onClick = { onSend(text.trim()); text = "" },
            enabled = canSend
        ) { Text("Send") }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatTs(ts: String): String =
    runCatching {
        OffsetDateTime.parse(ts).format(DateTimeFormatter.ofPattern("MMM d, HH:mm"))
    }.getOrDefault(ts)
