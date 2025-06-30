package com.nbk.insights.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nbk.insights.ui.theme.NBKBlue

// ENHANCED RecommendationMessageCard with Typography Dials
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
                        fontSize = 18.sp,                    // 🎛️ DIAL 7: Header size (16sp → 18sp)
                        fontWeight = FontWeight.ExtraBold,   // 🎛️ DIAL 8: Header weight (Bold → ExtraBold)
                        color = Color.White
                    )
                    Text(
                        message,
                        fontSize = 15.sp,                       // 🎛️ DIAL 9: Message size (14sp → 15sp)
                        fontWeight = FontWeight.Medium,         // 🎛️ DIAL 10: Message weight (added)
                        color = Color.White.copy(alpha = 0.95f), // 🎛️ DIAL 11: Message opacity (0.9f → 0.95f)
                        lineHeight = 22.sp                      // 🎛️ DIAL 12: Line height (20sp → 22sp)
                    )
                }
            }
        }
    }
}

// 🎛️ READABILITY DIAL REFERENCE:
/*
RecommendationMessageCard Dials:
DIAL 7 - Header Size: 18.sp (increased prominence)
DIAL 8 - Header Weight: ExtraBold (maximum impact)
DIAL 9 - Message Size: 15.sp (better readability)
DIAL 10 - Message Weight: Medium (added structure)
DIAL 11 - Message Opacity: 0.95f (clearer text)
DIAL 12 - Line Height: 22.sp (better text spacing)
*/