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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.nbk.insights.data.dtos.OfferResponse

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
            // Background Image with SubcomposeAsyncImage
            SubcomposeAsyncImage(
                model = offer.imageUrl,
                contentDescription = offer.description,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                loading = {
                    // Loading state
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        categoryColor.copy(alpha = 0.3f),
                                        categoryColor.copy(alpha = 0.1f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = categoryColor,
                            strokeWidth = 2.dp
                        )
                    }
                },
                error = {
                    // Error or no image fallback
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        categoryColor.copy(alpha = 0.3f),
                                        categoryColor.copy(alpha = 0.1f)
                                    )
                                )
                            )
                    )
                }
            )

            // 🎛️ DIAL 1: Gradient Overlay Strength
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.85f), // 🎛️ DIAL: Increase for darker overlay (0.5f to 0.9f)
                                Color.Black.copy(alpha = 0.7f)   // 🎛️ DIAL: Bottom opacity (0.6f to 0.95f)
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
                        fontSize = 16.sp,                    // 🎛️ DIAL 2: Main text size (14sp to 18sp)
                        fontWeight = FontWeight.Bold,        // 🎛️ DIAL 3: Font weight (Medium/SemiBold/Bold/ExtraBold)
                        color = Color.White,
                        maxLines = 2
                    )

                    // Show subcategory if available
                    offer.subCategory?.let { subCategory ->
                        Text(
                            subCategory,
                            fontSize = 13.sp,                       // 🎛️ DIAL 4: Subtitle size (11sp to 15sp)
                            fontWeight = FontWeight.SemiBold,       // 🎛️ DIAL 5: Subtitle weight (Normal/Medium/SemiBold/Bold)
                            color = Color.White.copy(alpha = 0.9f), // 🎛️ DIAL 6: Subtitle opacity (0.7f to 1.0f)
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
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
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