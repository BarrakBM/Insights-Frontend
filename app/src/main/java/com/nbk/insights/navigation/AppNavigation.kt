package com.nbk.insights.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.unit.IntOffset
import com.google.accompanist.navigation.animation.AnimatedNavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.nbk.insights.data.tempfunctions.getBankCards
import com.nbk.insights.ui.MainLayout
import com.nbk.insights.ui.composables.CardInsightContent
import com.nbk.insights.ui.screens.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(navController: NavHostController) {

    var currentRoute by remember { mutableStateOf(Screen.Login.route) }

    /* specs reused everywhere */
    val fadeSpec  = tween<Float>(450)
    val scaleSpec = tween<Float>(450)
    val slideSpec = tween<IntOffset>(450)

    /* ---------- Cross-fade + scale helpers ---------- */
    fun AnimatedContentTransitionScope<*>.crossFadeIn() =
        fadeIn(fadeSpec) + scaleIn(initialScale = 0.92f, animationSpec = scaleSpec)

    fun AnimatedContentTransitionScope<*>.crossFadeOut() =
        fadeOut(fadeSpec) + scaleOut(targetScale = 1.05f, animationSpec = scaleSpec)

    /* ---------- Horizontal slide helpers (for Login ↔ Home only) ---------- */
    fun AnimatedContentTransitionScope<*>.slideInFromRight() =
        slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, slideSpec) + fadeIn(fadeSpec)

    fun AnimatedContentTransitionScope<*>.slideOutToLeft() =
        slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, slideSpec) + fadeOut(fadeSpec)

    fun AnimatedContentTransitionScope<*>.slideInFromLeft() =
        slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, slideSpec) + fadeIn(fadeSpec)

    fun AnimatedContentTransitionScope<*>.slideOutToRight() =
        slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, slideSpec) + fadeOut(fadeSpec)

    /* ---------- NavHost ---------- */
    AnimatedNavHost(
        navController    = navController,
        startDestination = Screen.Login.route,
        /* global = cross-fade */
        enterTransition    = { crossFadeIn() },
        exitTransition     = { crossFadeOut() },
        popEnterTransition = { crossFadeIn() },
        popExitTransition  = { crossFadeOut() }
    ) {

        /* ─── LOGIN ───  (override: slide) */
        composable(
            route = Screen.Login.route,
            enterTransition = { crossFadeIn() },        // first screen just fades in
            exitTransition  = { slideOutToLeft() },     // leaving Login → Home slides left
            popEnterTransition = { slideInFromLeft() }, // back-press Home → Login slides right
            popExitTransition  = { crossFadeOut() }
        ) {
            LoginScreen(navController)
            currentRoute = Screen.Login.route
        }

        /* ─── HOME ───  (override only if coming from Login) */
        composable(
            route = Screen.Home.route,
            enterTransition = {
                if (currentRoute == Screen.Login.route)  slideInFromRight() else crossFadeIn()
            },
            exitTransition = {
                if (targetState.destination.route == Screen.Login.route) slideOutToRight() else crossFadeOut()
            }
        ) {
            HomeScreen(navController)
            currentRoute = Screen.Home.route
        }

        /* ─── INSIGHTS (cross-fade) ─── */
        composable(Screen.Insights.route,
            enterTransition = { crossFadeIn() },
            exitTransition  = { crossFadeOut() }
        ) {
            InsightsScreen(navController)
            currentRoute = Screen.Insights.route
        }

        /* ─── RECURRING PAYMENTS (cross-fade) ─── */
        composable(Screen.RecurringPayments.route,
            enterTransition = { crossFadeIn() },
            exitTransition  = { crossFadeOut() }
        ) {
            RecurringPaymentsScreen(navController)
            currentRoute = Screen.RecurringPayments.route
        }

        /* ─── Remaining screens inherit global cross-fade ─── */
        composable(Screen.Home2.route) {
            MainLayout(selectedTab = "Home", navController = navController) { padding ->
                HomeScreen2(navController = navController, paddingValues = padding)
            }
        }

        composable(Screen.AllTransactions.route) {
            AllTransactionsScreen(navController)
            currentRoute = Screen.AllTransactions.route
        }

        composable(Screen.AccountTransactions.route) { backStack ->
            backStack.arguments?.getString("accountId")?.toLongOrNull()?.let {
                AllTransactionsScreen(navController, it)
            }
            currentRoute = Screen.AccountTransactions.route
        }
        composable(Screen.Insights.route) {
            InsightsScreen(navController)
        }
        composable(Screen.Insights2.route) {
            MainLayout(selectedTab = "Insights", navController = navController) { padding ->
                InsightsScreen2(navController = navController, paddingValues = padding)
            }
        }
        composable(Screen.Notifications.route) {
            NotificationScreen(navController)
            currentRoute = Screen.Notifications.route
        }

        composable(Screen.Insights2.route) {
            InsightsScreen2(navController)
            currentRoute = Screen.Insights2.route
        }

        /* modal-style card insight */
        composable("card_insights/{cardId}") { backStack ->
            val id = backStack.arguments?.getString("cardId")
            val card = getBankCards().find { it.lastFourDigits == id }
            card?.let {
                CardInsightContent(card = it, onDismiss = {
                    navController.popBackStack()
                })
            }
            currentRoute = "card_insights/{cardId}"
        }
    }
}