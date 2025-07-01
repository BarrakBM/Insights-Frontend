package com.nbk.insights.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nbk.insights.data.dtos.OfferResponse
import com.nbk.insights.ui.composables.CategoryOfferCard
import com.nbk.insights.ui.screens.getCategoryColors
import com.nbk.insights.ui.screens.getCategoryIcons
import com.nbk.insights.ui.theme.TextPrimary
import com.nbk.insights.ui.theme.TextSecondary

@Composable
fun CategoryOffersSection(
    category: String,
    offers: List<OfferResponse>
) {
    val categoryDisplayName = category.lowercase().replaceFirstChar { it.uppercase() }
    val categoryIcon = getCategoryIcons(category)
    val categoryColor = getCategoryColors(category)

    // Create gradient colors for the category header icon
    val gradientColors = createCategoryGradient(categoryColor)

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Category header icon with subtle light background and colored icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = categoryColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    categoryIcon,
                    contentDescription = null,
                    tint = gradientColors[1], // Use the darker gradient color for the icon
                    modifier = Modifier.size(26.dp)
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

// Helper function to create gradient colors for each category
private fun createCategoryGradient(baseColor: Color): List<Color> {
    // Create a darker version of the base color for gradient effect
    val darkerColor = Color(
        red = (baseColor.red * 0.8f).coerceIn(0f, 1f),
        green = (baseColor.green * 0.8f).coerceIn(0f, 1f),
        blue = (baseColor.blue * 0.8f).coerceIn(0f, 1f),
        alpha = baseColor.alpha
    )

    return listOf(baseColor, darkerColor)
}