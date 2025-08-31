package com.aim.lifemate.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aim.lifemate.ui.theme.*

@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Card(
        onClick = onClick ?: {},
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.1f),
                        Color.White.copy(alpha = 0.05f)
                    )
                )
            )
            .border(
                1.dp,
                Brush.linearGradient(
                    colors = listOf(
                        NeonBlue.copy(alpha = 0.3f),
                        NeonPink.copy(alpha = 0.2f)
                    )
                ),
                RoundedCornerShape(20.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .background(Color.Transparent)
                .padding(16.dp)
        ) {
            content()
        }
    }
}

@Composable
fun NeonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = if (enabled) {
                    Brush.linearGradient(listOf(NeonBlue, NeonPink))
                } else {
                    Brush.linearGradient(listOf(Color.Gray, Color.DarkGray))
                }
            )
            .clickable(enabled = enabled && !isLoading, onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                color = if (enabled) Color.Black else Color.White.copy(alpha = 0.7f),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onNavigationSelected: (String) -> Unit
) {
    NavigationBar(
        containerColor = Color.Transparent,
        modifier = Modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.9f),
                        Color.Black.copy(alpha = 0.7f)
                    )
                )
            )
            .border(
                1.dp,
                Brush.linearGradient(listOf(NeonBlue.copy(alpha = 0.3f), NeonPink.copy(alpha = 0.2f))),
                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
    ) {
        listOf(
            NavItem("Wellness", Icons.Default.Spa, "wellness"),
            NavItem("Jobs", Icons.Default.Work, "jobs"),
            NavItem("Games", Icons.Default.SportsEsports, "games"),
            NavItem("AI", Icons.Default.SmartToy, "ai"),
            NavItem("Wallet", Icons.Default.AccountBalanceWallet, "wallet")
        ).forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = { onNavigationSelected(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NeonGreen,
                    selectedTextColor = NeonGreen,
                    unselectedIconColor = Color.White.copy(alpha = 0.7f),
                    unselectedTextColor = Color.White.copy(alpha = 0.7f),
                    indicatorColor = NeonBlue.copy(alpha = 0.2f)
                )
            )
        }
    }
}

data class NavItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String
)

@Composable
fun LoadingShimmer() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.1f),
                        Color.White.copy(alpha = 0.2f),
                        Color.White.copy(alpha = 0.1f)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
    )
}

@Composable
fun ErrorMessage(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Oops!",
            color = NeonPink,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = message,
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.padding(vertical = 8.dp)
        )
        NeonButton(
            text = "Try Again",
            onClick = onRetry,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}