package com.nbk.insights.ui.screens

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.nbk.insights.data.dtos.OfferBrief
import com.nbk.insights.data.dtos.OfferResponse
import com.nbk.insights.ui.composables.CategoryOffersSection
import com.nbk.insights.ui.composables.EmptyStateCard
import com.nbk.insights.ui.composables.ErrorCard
import com.nbk.insights.ui.composables.OffersSection
import com.nbk.insights.ui.composables.RecommendationMessageCard
import com.nbk.insights.ui.theme.*
import com.nbk.insights.utils.AppInitializer
import com.nbk.insights.viewmodels.RecommendationsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(navController: NavController, paddingValues: PaddingValues) {
    val activity = LocalActivity.current as ComponentActivity
    val recommendationsVM: RecommendationsViewModel = viewModel(
        viewModelStoreOwner = activity,
        factory = remember { AppInitializer.provideRecommendationsViewModelFactory(activity) }
    )

    // State variables
    val offersRecommendation by recommendationsVM.offersRecommendation
    val allCategoryOffers by recommendationsVM.allCategoryOffers
    val isLoading by recommendationsVM.isLoading
    val errorMessage by recommendationsVM.errorMessage

    // Categories to display - updated to match your database
    val categories = listOf("Dining", "Food & Groceries", "Entertainment", "Shopping", "Other")

    // Fetch data only if we don't have it cached
    LaunchedEffect(Unit) {
        recommendationsVM.fetchExploreScreenData(forceRefresh = false)
    }

    // Add pull-to-refresh
    val pullToRefreshState = androidx.compose.material3.pulltorefresh.rememberPullToRefreshState()

    androidx.compose.material3.pulltorefresh.PullToRefreshBox(
        isRefreshing = isLoading,
        onRefresh = {
            recommendationsVM.fetchExploreScreenData(forceRefresh = true)
        },
        state = pullToRefreshState,
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(paddingValues)
    ) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    Text(
                        "Explore Offers",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        "Discover personalized offers and recommendations",
                        fontSize = 16.sp,
                        color = TextSecondary
                    )
                }
            }

            // Loading state (only show if no data is cached)
            if (isLoading && offersRecommendation == null && allCategoryOffers.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = NBKBlue)
                    }
                }
            } else {
                // Recommendation Message Card
                offersRecommendation?.message?.let { message ->
                    item {
                        RecommendationMessageCard(message = message)
                    }
                }

                // Relevant Offers Section
                offersRecommendation?.offers?.let { offers ->
                    if (offers.isNotEmpty()) {
                        item {
                            OffersSection(
                                title = "Recommended for You",
                                subtitle = "Based on your spending patterns",
                                offers = offers,
                                isRelevantOffers = true
                            )
                        }
                    }
                }

                // Category Offers Sections
                categories.forEach { category ->
                    allCategoryOffers[category]?.let { offers ->
                        if (offers.isNotEmpty()) {
                            item {
                                CategoryOffersSection(
                                    category = category,
                                    offers = offers
                                )
                            }
                        }
                    }
                }

                // Error state
                errorMessage?.let { error ->
                    item {
                        ErrorCard(error = error) {
                            recommendationsVM.fetchExploreScreenData(forceRefresh = true)
                        }
                    }
                }

                // Empty state
                if (!isLoading && offersRecommendation?.offers?.isEmpty() == true && allCategoryOffers.isEmpty()) {
                    item {
                        EmptyStateCard()
                    }
                }
            }
        }
    }
}

// Helper functions for category icons and colors - updated to match your database
fun getCategoryIcons(category: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (category) {
        "Entertainment" -> Icons.Default.Movie
        "Dining" -> Icons.Default.Restaurant
        "Shopping" -> Icons.Default.ShoppingBag
        "Food & Groceries" -> Icons.Default.ShoppingCart
        "Other" -> Icons.Default.Category
        else -> Icons.Default.Category
    }
}

fun getCategoryColors(category: String): Color {
    return when (category) {
        "Entertainment" -> CategoryEntertainment
        "Dining" -> CategoryDining
        "Shopping" -> CategoryShopping
        "Food & Groceries" -> Color(0xFFF59E0B) // Orange for groceries
        "Other" -> Gray500
        else -> Gray500
    }
}