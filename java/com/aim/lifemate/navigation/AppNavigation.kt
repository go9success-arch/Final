package com.aim.lifemate.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aim.lifemate.services.VoiceSearchService
import com.aim.lifemate.ui.screens.MainDashboardScreen
import com.aim.lifemate.ui.screens.ProfileScreen
import com.aim.lifemate.ui.screens.SplashScreen
import com.aim.lifemate.ui.screens.ai.AIAssistantScreen
import com.aim.lifemate.ui.screens.ai.CareerAdviceScreen
import com.aim.lifemate.ui.screens.ai.StockInsightsScreen
import com.aim.lifemate.ui.screens.games.CubeJumperScreen
import com.aim.lifemate.ui.screens.games.LeaderboardScreen
import com.aim.lifemate.ui.screens.games.TournamentScreen
import com.aim.lifemate.ui.screens.jobs.GovernmentJobsScreen
import com.aim.lifemate.ui.screens.jobs.JobDetailScreen
import com.aim.lifemate.ui.screens.jobs.JobsDashboardScreen
import com.aim.lifemate.ui.screens.jobs.PrivateJobsScreen
import com.aim.lifemate.ui.screens.notifications.NotificationsScreen
import com.aim.lifemate.ui.screens.wallet.WalletScreen
import com.aim.lifemate.ui.screens.wallet.WithdrawScreen
import com.aim.lifemate.ui.screens.wellness.WellnessDetailScreen
import com.aim.lifemate.ui.screens.wellness.WellnessSearchScreen
import com.aim.lifemate.utils.LanguageManager
import com.aim.lifemate.viewmodel.WellnessViewModel

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    voiceSearchService: VoiceSearchService,
    languageManager: LanguageManager
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screens.Splash.route,
        modifier = modifier
    ) {
        composable(Screens.Splash.route) {
            SplashScreen(navController = navController)
        }

        composable(Screens.Main.route) {
            MainDashboardScreen(navController = navController)
        }

        // Wellness Screens
        composable(Screens.WellnessSearch.route) {
            val viewModel: WellnessViewModel = hiltViewModel()
            WellnessSearchScreen(
                navController = navController,
                viewModel = viewModel,
                voiceSearchService = voiceSearchService,
                languageManager = languageManager
            )
        }

        composable(
            route = "${Screens.WellnessDetail.route}/{remedyId}",
            arguments = listOf(navArgument("remedyId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val remedyId = backStackEntry.arguments?.getString("remedyId") ?: ""
            WellnessDetailScreen(
                remedyId = remedyId,
                navController = navController
            )
        }

        // Jobs Screens
        composable(Screens.JobsDashboard.route) {
            JobsDashboardScreen(navController = navController)
        }

        composable(Screens.GovtJobs.route) {
            GovernmentJobsScreen(navController = navController)
        }

        composable(Screens.PrivateJobs.route) {
            PrivateJobsScreen(navController = navController)
        }

        composable(
            route = "${Screens.JobDetail.route}/{jobType}/{jobId}",
            arguments = listOf(
                navArgument("jobType") {
                    type = NavType.StringType
                },
                navArgument("jobId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val jobType = backStackEntry.arguments?.getString("jobType") ?: ""
            val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
            JobDetailScreen(
                navController = navController,
                jobType = jobType,
                jobId = jobId
            )
        }

        // Games Screens
        composable(Screens.CubeJumper.route) {
            CubeJumperScreen(navController = navController)
        }

        composable(Screens.Leaderboard.route) {
            LeaderboardScreen(navController = navController)
        }

        composable(Screens.Tournament.route) {
            TournamentScreen(navController = navController)
        }

        // AI Screens
        composable(Screens.AIAssistant.route) {
            AIAssistantScreen(navController = navController)
        }

        composable(Screens.CareerAdvice.route) {
            CareerAdviceScreen(navController = navController)
        }

        composable(Screens.StockInsights.route) {
            StockInsightsScreen(navController = navController)
        }

        // Wallet Screens
        composable(Screens.Wallet.route) {
            WalletScreen(navController = navController)
        }

        composable(Screens.Withdraw.route) {
            WithdrawScreen(navController = navController)
        }

        // Profile & Notifications
        composable(Screens.Profile.route) {
            ProfileScreen(navController = navController)
        }

        composable(Screens.Notifications.route) {
            NotificationsScreen(navController = navController)
        }
    }
}

sealed class Screens(val route: String) {
    object Splash : Screens("splash")
    object Main : Screens("main")

    // Wellness
    object WellnessSearch : Screens("wellness/search")
    object WellnessDetail : Screens("wellness/detail")

    // Jobs
    object JobsDashboard : Screens("jobs/dashboard")
    object GovtJobs : Screens("jobs/govt")
    object PrivateJobs : Screens("jobs/private")
    object JobDetail : Screens("jobs/detail")

    // Games
    object CubeJumper : Screens("games/cube_jumper")
    object Leaderboard : Screens("games/leaderboard")
    object Tournament : Screens("games/tournament")

    // AI
    object AIAssistant : Screens("ai/assistant")
    object CareerAdvice : Screens("ai/career")
    object StockInsights : Screens("ai/stocks")

    // Wallet
    object Wallet : Screens("wallet")
    object Withdraw : Screens("withdraw")

    // Profile & Notifications
    object Profile : Screens("profile")
    object Notifications : Screens("notifications")
}