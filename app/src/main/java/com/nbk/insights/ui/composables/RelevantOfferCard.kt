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
import androidx.compose.material.icons.filled.Lightbulb
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.nbk.insights.data.dtos.OfferBrief
import com.nbk.insights.ui.theme.NBKBlue

// IMPROVED RelevantOfferCard with Readability Dials
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
            // Background Image with SubcomposeAsyncImage for better loading states
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
                                        NBKBlue.copy(alpha = 0.3f),
                                        NBKBlue.copy(alpha = 0.1f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = NBKBlue,
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
                                        NBKBlue.copy(alpha = 0.3f),
                                        NBKBlue.copy(alpha = 0.1f)
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
                                Color.Black.copy(alpha = 0.85f), // 🎛️ DIAL: Stronger overlay (0.75f → 0.85f)
                                Color.Black.copy(alpha = 0.7f)   // 🎛️ DIAL: Bottom opacity (0.6f → 0.9f)
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
                        fontSize = 18.sp,                    // 🎛️ DIAL 2: Main text size (16sp → 18sp)
                        fontWeight = FontWeight.Bold,        // 🎛️ DIAL 3: Font weight (Medium → Bold)
                        color = Color.White,
                        maxLines = 2
                    )
                    offer.subCategory?.let { subCategory ->
                        Text(
                            subCategory,
                            fontSize = 14.sp,                       // 🎛️ DIAL 4: Subtitle size (12sp → 14sp)
                            fontWeight = FontWeight.SemiBold,       // 🎛️ DIAL 5: Subtitle weight (added)
                            color = Color.White.copy(alpha = 0.95f), // 🎛️ DIAL 6: Subtitle opacity (0.9f → 0.95f)
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
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// 🎛️ READABILITY DIAL REFERENCE:
/*
RelevantOfferCard Dials:
DIAL 1 - Gradient Overlay: 0.85f, 0.9f (stronger than before)
DIAL 2 - Main Text Size: 18.sp (bigger for prominence)
DIAL 3 - Main Text Weight: Bold (stronger than Medium)
DIAL 4 - Subtitle Size: 14.sp (bigger for readability)
DIAL 5 - Subtitle Weight: SemiBold (added weight)
DIAL 6 - Subtitle Opacity: 0.95f (higher visibility)
 */