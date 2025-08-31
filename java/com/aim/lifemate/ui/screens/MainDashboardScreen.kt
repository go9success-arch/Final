package com.aim.lifemate.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.aim.lifemate.data.models.GovernmentJob
import com.aim.lifemate.data.models.PrivateJob
import com.aim.lifemate.data.models.Tournament
import com.aim.lifemate.data.models.WellnessRemedy
import com.aim.lifemate.viewmodel.MainViewModel


@Composable
fun MainDashboardScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Welcome Section
            item {
                Text(
                    text = "Welcome, ${state.userProfile.name}!",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                WalletBalanceCard(balance = state.walletBalance)
            }

            // Featured Wellness Practices
            item {
                Text(
                    text = "Wellness Practices",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
            items(state.featuredWellness) { wellness ->
                WellnessItem(wellness = wellness, navController = navController)
            }

            // Job Opportunities
            item {
                Text(
                    text = "Job Opportunities",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
            items(state.jobOpportunities) { job ->
                when (job) {
                    is GovernmentJob -> GovernmentJobItem(job = job, navController = navController)
                    is PrivateJob -> PrivateJobItem(job = job, navController = navController)
                }
            }

            // Tournament Info
            state.tournamentInfo?.let { tournament ->
                item {
                    TournamentCard(
                        tournament = tournament,
                        onParticipate = { viewModel.participateInTournament(tournament.id) },
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }
        }
    }

    // Error Handling
    state.error?.let { error ->
        LaunchedEffect(error) {
            // Show snackbar or dialog with error
        }
    }
}

@Composable
fun WalletBalanceCard(balance: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Wallet Balance",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "₹${balance}",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun WellnessItem(wellness: WellnessRemedy, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = {
            navController.navigate("wellness/detail/${wellness.id}")
        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = wellness.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = wellness.description.take(100) + "...",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun GovernmentJobItem(job: GovernmentJob, navController: NavController) {
    JobItem(
        title = job.title,
        company = job.department,
        location = job.location,
        onClick = {
            navController.navigate("jobs/detail/government/${job.id}")
        }
    )
}

@Composable
fun PrivateJobItem(job: PrivateJob, navController: NavController) {
    JobItem(
        title = job.title,
        company = job.companyName,
        location = job.location,
        onClick = {
            navController.navigate("jobs/detail/private/${job.id}")
        }
    )
}

@Composable
fun JobItem(title: String, company: String, location: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = company,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = location,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun TournamentCard(tournament: Tournament, onParticipate: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = tournament.title,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Prize: ₹${tournament.prizePool}",
                style = MaterialTheme.typography.bodyMedium
            )
            Button(
                onClick = onParticipate,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Join Tournament")
            }
        }
    }
}