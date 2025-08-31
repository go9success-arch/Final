package com.aim.lifemate.ui.screens.jobs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.aim.lifemate.data.models.GovernmentJob
import com.aim.lifemate.data.models.PrivateJob
import com.aim.lifemate.viewmodel.JobsViewModel

// Required imports for property delegate syntax
import androidx.compose.runtime.getValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailScreen(
    navController: NavHostController,
    jobId: String,
    jobType: String,
    viewModel: JobsViewModel = hiltViewModel()
) {
    // CORRECT: Use property delegate syntax with the fixed ViewModel
    val state by viewModel.state

    val job = remember(jobId, jobType, state) {
        when (jobType) {
            "government" -> state.governmentJobs.find { it.id == jobId }
            "private" -> state.privateJobs.find { it.id == jobId }
            else -> null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(job?.let { getJobTitle(it) } ?: "Job Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            job?.let {
                ExtendedFloatingActionButton(
                    onClick = { /* Apply for job */ },
                    icon = { Icon(Icons.Default.Send, contentDescription = "Apply") },
                    text = { Text("Apply Now") }
                )
            }
        }
    ) { padding ->
        if (job == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Job not found",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        } else {
            JobDetailContent(job = job, modifier = Modifier.padding(padding))
        }
    }
}

@Composable
fun JobDetailContent(job: Any, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = getJobTitle(job),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Company/Department
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Work,
                        contentDescription = "Company",
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = getCompanyDepartment(job),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }

                // Location
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "Location",
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = getLocation(job),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }

                // Salary
                Text(
                    text = "Salary: ${getSalary(job)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // Details Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Job Description",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = getDescription(job),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }

        // Requirements Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Requirements",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = getRequirements(job),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }

        // Additional Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Additional Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Deadline (for government jobs)
                if (job is GovernmentJob) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = "Deadline",
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Application Deadline: ${job.applicationDeadline}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Job Type (for private jobs)
                if (job is PrivateJob) {
                    Text(
                        text = "Job Type: ${job.jobType}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Application Info
                Text(
                    text = "How to Apply: ${getApplicationInfo(job)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    }
}

// Helper functions to extract job information
private fun getJobTitle(job: Any): String {
    return when (job) {
        is GovernmentJob -> job.title
        is PrivateJob -> job.title
        else -> "Job Title"
    }
}

private fun getCompanyDepartment(job: Any): String {
    return when (job) {
        is GovernmentJob -> job.department
        is PrivateJob -> job.company
        else -> "Company/Department"
    }
}

private fun getLocation(job: Any): String {
    return when (job) {
        is GovernmentJob -> job.location
        is PrivateJob -> job.location
        else -> "Location"
    }
}

private fun getSalary(job: Any): String {
    return when (job) {
        is GovernmentJob -> job.salary
        is PrivateJob -> job.salary
        else -> "Salary information"
    }
}

private fun getDescription(job: Any): String {
    return when (job) {
        is GovernmentJob -> job.description
        is PrivateJob -> job.description
        else -> "Job description not available"
    }
}

private fun getRequirements(job: Any): String {
    return when (job) {
        is GovernmentJob -> job.qualifications
        is PrivateJob -> job.requirements
        else -> "Requirements not specified"
    }
}

private fun getApplicationInfo(job: Any): String {
    return when (job) {
        is GovernmentJob -> "Visit: ${job.applicationLink}"
        is PrivateJob -> "Email: ${job.applicationEmail}"
        else -> "Contact for application details"
    }
}