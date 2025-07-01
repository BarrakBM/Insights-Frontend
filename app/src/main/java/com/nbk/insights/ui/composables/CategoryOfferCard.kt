package com.nbk.insights.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.nbk.insights.data.dtos.OfferResponse
import com.nbk.insights.ui.theme.TextPrimary
import com.nbk.insights.ui.theme.TextSecondary

@Composable
fun CategoryOfferCard(
    offer: OfferResponse,
    categoryColor: Color
) {
    // Create gradient colors for the category
    val gradientColors = createCategoryGradient(categoryColor)

    Card(
        modifier = Modifier
            .width(240.dp)
            .height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon with gradient background
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = gradientColors
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    getCategoryIcon(offer.subCategory ?: "Other"),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    offer.description,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )

                offer.subCategory?.let { subCategory ->
                    Text(
                        subCategory,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            // Arrow icon with gradient color
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "View offer",
                tint = gradientColors[1], // Use the darker color from gradient
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// Helper function to get appropriate icon
private fun getCategoryIcon(subCategory: String): ImageVector {
    return when (subCategory.lowercase()) {
        "coffee", "cafe" -> Icons.Default.Coffee
        "restaurant", "dining" -> Icons.Default.Restaurant
        "shopping", "retail" -> Icons.Default.ShoppingBag
        "grocery", "supermarket" -> Icons.Default.ShoppingCart
        "entertainment", "movie" -> Icons.Default.Movie
        else -> Icons.Default.LocalOffer
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

// 🎛️ READABILITY DIAL REFERENCE:
/*
DIAL 1 - Gradient Overlay Strength:
- Light overlay: 0.5f, 0.6f
- Medium overlay: 0.75f, 0.8f
- Strong overlay: 0.85f, 0.9f
- Maximum overlay: 0.9f, 0.95f

DIAL 2 - Main Text Size:
- Small: 14.sp
- Medium: 16.sp (recommended)
- Large: 18.sp

DIAL 3 - Main Text Weight:
- FontWeight.Medium (lighter)
- FontWeight.SemiBold
- FontWeight.Bold (recommended)
- FontWeight.ExtraBold (heaviest)

DIAL 4 - Subtitle Size:
- Small: 11.sp
- Medium: 13.sp (recommended)
- Large: 15.sp

DIAL 5 - Subtitle Weight:
- FontWeight.Normal
- FontWeight.Medium
- FontWeight.SemiBold (recommended)
- FontWeight.Bold

DIAL 6 - Subtitle Opacity:
- Subtle: 0.7f
- Medium: 0.8f
- Strong: 0.9f (recommended)
- Full: 1.0f
*/