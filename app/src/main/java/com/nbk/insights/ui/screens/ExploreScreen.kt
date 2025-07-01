package com.nbk.insights.ui.screens

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
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

    // Filter state
    var selectedCategory by remember { mutableStateOf("All") }
    var showCategoryDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Filter categories based on selection
    val filteredCategories = if (selectedCategory != "All") {
        listOf(selectedCategory)
    } else {
        categories
    }

    // Search and filter categories with offers that have matching content
    val searchedCategories by remember(searchQuery, filteredCategories, allCategoryOffers) {
        derivedStateOf {
            if (searchQuery.isNotEmpty()) {
                filteredCategories.filter { category ->
                    category.contains(searchQuery, ignoreCase = true) ||
                            allCategoryOffers[category]?.any { offer ->
                                offer.toString().contains(searchQuery, ignoreCase = true)
                            } == true
                }
            } else {
                filteredCategories
            }
        }
    }

    // Filter recommended offers based on search
    val filteredRecommendedOffers by remember(searchQuery, offersRecommendation) {
        derivedStateOf {
            if (searchQuery.isEmpty()) {
                offersRecommendation?.offers ?: emptyList()
            } else {
                offersRecommendation?.offers?.filter { offer ->
                    offer.toString().contains(searchQuery, ignoreCase = true)
                } ?: emptyList()
            }
        }
    }

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

            // Search Bar and Category Filter
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                "Search offers...",
                                fontSize = 14.sp,
                                color = TextSecondary
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = TextSecondary
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "Clear search",
                                        tint = TextSecondary
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NBKBlue,
                            unfocusedBorderColor = Gray300,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        )
                    )

                    // Category Filter Button
                    OutlinedButton(
                        onClick = { showCategoryDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = NBKBlue
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NBKBlue)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedCategory == "All") "Filter" else selectedCategory,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Recommendation Message Card (only show when All is selected and no search)
            if (selectedCategory == "All" && searchQuery.isEmpty()) {
                offersRecommendation?.message?.let { message ->
                    item {
                        RecommendationMessageCard(message = message)
                    }
                }
            }

            // Relevant Offers Section (only show when All is selected)
            if (selectedCategory == "All" && filteredRecommendedOffers.isNotEmpty()) {
                item {
                    OffersSection(
                        title = "Recommended for You",
                        subtitle = if (searchQuery.isEmpty()) "Based on your spending patterns" else "Search results in recommended offers",
                        offers = filteredRecommendedOffers,
                        isRelevantOffers = true
                    )
                }
            }

            // Category Offers Sections
            items(searchedCategories) { category ->
                allCategoryOffers[category]?.let { offers ->
                    val filteredOffers = if (searchQuery.isNotEmpty()) {
                        // If searching for category name, show all offers in that category
                        if (category.contains(searchQuery, ignoreCase = true)) {
                            offers
                        } else {
                            // Otherwise filter offers by content
                            offers.filter { offer ->
                                offer.toString().contains(searchQuery, ignoreCase = true)
                            }
                        }
                    } else {
                        offers
                    }

                    if (filteredOffers.isNotEmpty()) {
                        CategoryOffersSection(
                            category = category,
                            offers = filteredOffers
                        )
                    }
                }
            }

            // Clear Filter Button (show when specific category is selected)
            if (selectedCategory != "All") {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        OutlinedButton(
                            onClick = {
                                selectedCategory = "All"
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = NBKBlue
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NBKBlue)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear Filter",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Clear Filter", fontSize = 14.sp)
                        }
                    }
                }
            }

            // No search results message
            if (searchQuery.isNotEmpty() && filteredRecommendedOffers.isEmpty() && searchedCategories.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "No results",
                                modifier = Modifier.size(64.dp),
                                tint = Gray400
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "No offers found",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "Try adjusting your search or filter criteria",
                                fontSize = 14.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp)
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

    // Category Selection Dialog
    if (showCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showCategoryDialog = false },
            title = {
                Text(
                    "Filter by Category",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                LazyColumn {
                    // All Categories option
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedCategory = "All"
                                    showCategoryDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedCategory == "All",
                                onClick = {
                                    selectedCategory = "All"
                                    showCategoryDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = NBKBlue)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "All Categories",
                                fontSize = 16.sp,
                                color = TextPrimary
                            )
                        }
                    }

                    // Individual category options
                    items(categories) { category ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedCategory = category
                                    showCategoryDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedCategory == category,
                                onClick = {
                                    selectedCategory = category
                                    showCategoryDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = NBKBlue)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = getCategoryIcons(category),
                                contentDescription = category,
                                tint = getCategoryColors(category),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                category,
                                fontSize = 16.sp,
                                color = TextPrimary
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showCategoryDialog = false }
                ) {
                    Text("Close", color = NBKBlue)
                }
            }
        )
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