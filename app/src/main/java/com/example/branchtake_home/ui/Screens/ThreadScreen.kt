package com.example.branchtake_home.ui.Screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.branchtake_home.ViewModel.ThreadItem
import com.example.branchtake_home.ViewModel.ThreadsEvent
import com.example.branchtake_home.ViewModel.ThreadsViewModel
import com.example.branchtake_home.ViewModel.UiState
import kotlinx.coroutines.flow.collectLatest
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreadsScreen(
    vm: ThreadsViewModel,
    onOpenThread: (threadId: Long, userId: Long) -> Unit,
    onLogout: () -> Unit
) {
    val state by vm.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Collect one-shot events from the VM (e.g., reset success/failure)
    LaunchedEffect(Unit) {
        vm.events.collectLatest { event ->
            when (event) {
                is ThreadsEvent.Snackbar -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        topBar = {
            var expanded by remember { mutableStateOf(false) }
            TopAppBar(
                title = { Text("Conversations") },
                actions = {
                    // Overflow menu (Reset + Logout)
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Reset My Sent Messages") },
                            onClick = {
                                expanded = false
                                vm.resetAgentMessages()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                expanded = false
                                onLogout()
                            }
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (val s = state) {
                is UiState.Loading -> CircularProgressIndicator(Modifier.padding(24.dp))
                is UiState.Error -> Column(Modifier.padding(24.dp)) {
                    Text("Failed to load: ${s.message}")
                    TextButton(onClick = vm::refresh) { Text("Retry") }
                }
                is UiState.Success -> ThreadsList(s.data, onOpenThread)
                else -> {}
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ThreadsList(items: List<ThreadItem>, onOpen: (Long, Long) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items) { item ->
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpen(item.threadId, item.userId ?: 0L) }
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text(
                        "Thread #${item.threadId} • User ${item.userId ?: "N/A"}",
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(item.last.body, maxLines = 2)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        formatTs(item.last.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatTs(ts: String): String =
    runCatching {
        OffsetDateTime.parse(ts).format(DateTimeFormatter.ofPattern("MMM d, HH:mm"))
    }.getOrDefault(ts)
