package com.aim.lifemate.ui.screens.jobs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.AccountBalance // Use this instead of Government
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun JobsDashboardScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Government Jobs Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clickable { navController.navigate("governmentJobs") },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.AccountBalance, "Government Jobs", modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Text("Government Jobs", style = MaterialTheme.typography.headlineSmall)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Private Jobs Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clickable { navController.navigate("privateJobs") },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Business, "Private Jobs", modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Text("Private Jobs", style = MaterialTheme.typography.headlineSmall)
            }
        }
    }
}