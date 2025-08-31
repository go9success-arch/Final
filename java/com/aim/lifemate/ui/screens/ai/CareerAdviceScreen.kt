package com.aim.lifemate.ui.screens.ai

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.aim.lifemate.viewmodel.AIViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareerAdviceScreen(navController: NavHostController) {
    val viewModel: AIViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Career Advisor") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Quick Advice Topics
            Text(
                "Get Career Advice On:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            val topics = listOf(
                "Resume Writing Tips",
                "Interview Preparation",
                "Career Change Advice",
                "Skill Development",
                "Salary Negotiation",
                "Job Search Strategies"
            )

            LazyVerticalGrid(
                columns = androidx.compose.foundation.layout.AdaptiveGridSize(150.dp),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(topics) { topic ->
                    TopicCard(topic) {
                        viewModel.sendMessage("Give me advice about $topic")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Custom Question Input
            var customQuestion by remember { mutableStateOf("") }

            OutlinedTextField(
                value = customQuestion,
                onValueChange = { customQuestion = it },
                placeholder = { Text("Ask a specific career question...") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (customQuestion.isNotBlank()) {
                                viewModel.sendMessage(customQuestion)
                                customQuestion = ""
                            }
                        },
                        enabled = customQuestion.isNotBlank()
                    ) {
                        Icon(Icons.Default.Send, "Send")
                    }
                }
            )
        }
    }
}

@Composable
fun TopicCard(topic: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .height(100.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Lightbulb,
                contentDescription = topic,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = topic,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}