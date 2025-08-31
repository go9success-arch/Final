package com.aim.lifemate.ui.screens.wellness

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.aim.lifemate.services.VoiceSearchService
import com.aim.lifemate.utils.LanguageManager
import com.aim.lifemate.viewmodel.WellnessViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WellnessSearchScreen(
    navController: NavHostController,
    viewModel: WellnessViewModel = hiltViewModel(),
    voiceSearchService: VoiceSearchService,
    languageManager: LanguageManager
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var showLanguageDialog by remember { mutableStateOf(false) }

    LaunchedEffect(voiceSearchService.recognizedText) {
        voiceSearchService.recognizedText.collectLatest { text ->
            if (text.isNotEmpty()) {
                viewModel.searchRemedies(text)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Natural Wellness Remedies") },
                actions = {
                    IconButton(onClick = { showLanguageDialog = true }) {
                        Icon(Icons.Default.Language, "Change Language")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    voiceSearchService.startVoiceInput(context as androidx.activity.ComponentActivity) { query ->
                        viewModel.searchRemedies(query)
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                val isListening by voiceSearchService.isListening.collectAsState()
                Icon(
                    if (isListening) Icons.Default.Mic else Icons.Default.MicNone,
                    "Voice Search"
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Search Bar with Voice Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.searchRemedies(it) },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Search remedies...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) }
                )
                IconButton(
                    onClick = {
                        voiceSearchService.startVoiceInput(context as androidx.activity.ComponentActivity) { query ->
                            viewModel.searchRemedies(query)
                        }
                    }
                ) {
                    val isListening by voiceSearchService.isListening.collectAsState()
                    Icon(
                        if (isListening) Icons.Default.Mic else Icons.Default.MicNone,
                        "Voice Search"
                    )
                }
            }

            // Remedies List
            if (state.filteredRemedies.isNotEmpty()) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.filteredRemedies) { remedy ->
                        WellnessRemedyCard(
                            remedy = remedy,
                            language = state.currentLanguage,
                            onClick = {
                                navController.navigate("wellness/detail/${remedy.id}")
                            }
                        )
                    }
                }
            } else if (!state.isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (state.searchQuery.isNotEmpty()) "No remedies found" else "Search for natural remedies",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // Loading State
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                ) {
                    CircularProgressIndicator()
                }
            }

            // Error State
            state.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            onDismiss = { showLanguageDialog = false },
            onLanguageSelected = { language ->
                viewModel.setLanguage(language)
                languageManager.setLanguage(language)
                showLanguageDialog = false
            },
            languageManager = languageManager
        )
    }
}

@Composable
fun LanguageSelectionDialog(
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit,
    languageManager: LanguageManager
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Language") },
        text = {
            Column {
                languageManager.supportedLanguages.forEach { languageCode ->
                    Button(
                        onClick = { onLanguageSelected(languageCode) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        Text(languageManager.getDisplayLanguage(languageCode))
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun WellnessRemedyCard(
    remedy: com.aim.lifemate.data.models.WellnessRemedy,
    language: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = remedy.getPracticeName(language),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "For: ${remedy.getWellnessFocus(language)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Region: ${remedy.getRegion(language)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Learn More →",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}