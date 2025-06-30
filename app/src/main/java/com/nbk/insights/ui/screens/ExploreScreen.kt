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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nbk.insights.data.dtos.OfferBrief
import com.nbk.insights.data.dtos.OfferResponse
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

@Composable
fun RecommendationMessageCard(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(NBKBlue, Color(0xFF1976D2))
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "Smart Recommendation",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        message,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun OffersSection(
    title: String,
    subtitle: String,
    offers: List<OfferBrief>,
    isRelevantOffers: Boolean = false
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            Text(
                title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                subtitle,
                fontSize = 14.sp,
                color = TextSecondary
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 20.dp)
        ) {
            items(offers) { offer ->
                RelevantOfferCard(offer = offer)
            }
        }
    }
}

@Composable
fun CategoryOffersSection(
    category: String,
    offers: List<OfferResponse>
) {
    val categoryDisplayName = category.lowercase().replaceFirstChar { it.uppercase() }
    val categoryIcon = getCategoryIcon(category)
    val categoryColor = getCategoryColor(category)

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(categoryColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    categoryIcon,
                    contentDescription = null,
                    tint = categoryColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    categoryDisplayName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    "${offers.size} offers available",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 20.dp)
        ) {
            items(offers) { offer ->
                CategoryOfferCard(
                    offer = offer,
                    categoryColor = categoryColor
                )
            }
        }
    }
}

@Composable
fun RelevantOfferCard(offer: OfferBrief) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(120.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background Image (placeholder for now)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        // Placeholder gradient - replace with actual image
                        Brush.verticalGradient(
                            colors = listOf(
                                NBKBlue.copy(alpha = 0.3f),
                                NBKBlue.copy(alpha = 0.1f)
                            )
                        )
                    )
            )

            // Gradient overlay for text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.9f),
                                Color.White
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top spacer to push content to bottom
                Spacer(modifier = Modifier.weight(1f))

                // Bottom content
                Column {
                    Text(
                        offer.description,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        maxLines = 2
                    )
                    offer.subCategory?.let { subCategory ->
                        Text(
                            subCategory,
                            fontSize = 12.sp,
                            color = NBKBlue,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = "View offer",
                        tint = NBKBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryOfferCard(
    offer: OfferResponse,
    categoryColor: Color
) {
    Card(
        modifier = Modifier
            .width(240.dp)
            .height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background Image (placeholder for now)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        // Placeholder gradient - replace with actual image
                        Brush.verticalGradient(
                            colors = listOf(
                                categoryColor.copy(alpha = 0.3f),
                                categoryColor.copy(alpha = 0.1f)
                            )
                        )
                    )
            )

            // Gradient overlay for text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.9f),
                                Color.White
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top spacer to push content to bottom
                Spacer(modifier = Modifier.weight(1f))

                // Bottom content
                Text(
                    offer.description,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    maxLines = 2
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = "View offer",
                        tint = categoryColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorCard(error: String, onRetry: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = null,
                tint = Error,
                modifier = Modifier.size(48.dp)
            )
            Text(
                "Something went wrong",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                error,
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = NBKBlue)
            ) {
                Text("Try Again")
            }
        }
    }
}

@Composable
fun EmptyStateCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.SearchOff,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(64.dp)
            )
            Text(
                "No offers available",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                "Check back later for new offers and recommendations",
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Helper functions for category icons and colors - updated to match your database
private fun getCategoryIcon(category: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (category) {
        "Entertainment" -> Icons.Default.Movie
        "Dining" -> Icons.Default.Restaurant
        "Shopping" -> Icons.Default.ShoppingBag
        "Food & Groceries" -> Icons.Default.ShoppingCart
        "Other" -> Icons.Default.Category
        else -> Icons.Default.Category
    }
}

private fun getCategoryColor(category: String): Color {
    return when (category) {
        "Entertainment" -> CategoryEntertainment
        "Dining" -> CategoryDining
        "Shopping" -> CategoryShopping
        "Food & Groceries" -> Color(0xFFF59E0B) // Orange for groceries
        "Other" -> Gray500
        else -> Gray500
    }
}