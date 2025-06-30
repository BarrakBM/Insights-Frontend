package com.nbk.insights.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nbk.insights.ui.theme.PrimaryBlue
import com.nbk.insights.ui.theme.SuccessGreen
import com.nbk.insights.ui.theme.TextPrimary
import com.nbk.insights.ui.theme.TextSecondary
import com.nbk.insights.ui.theme.WarningAmber

@Composable
fun SmartRecommendations() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            "Smart Recommendations",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )

        RecommendationCard(
            icon = Icons.Default.Warning,
            title = "Reduce Dining Expenses",
            description = "You're spending 25% more on dining than last month. Consider meal planning to save $80-120.",
            actionText = "Set Dining Goal",
            color = WarningAmber
        )

        RecommendationCard(
            icon = Icons.Default.Lightbulb,
            title = "Increase Emergency Fund",
            description = "With your current savings rate, you could build a 6-month emergency fund in 8 months.",
            actionText = "Start Auto-Save",
            color = SuccessGreen
        )

        RecommendationCard(
            icon = Icons.Default.TrendingUp,
            title = "Investment Opportunity",
            description = "Based on your surplus, consider investing $500/month in index funds for long-term growth.",
            actionText = "Learn More",
            color = PrimaryBlue
        )
    }
}

@Composable
fun RecommendationCard(
    icon: ImageVector,
    title: String,
    description: String,
    actionText: String,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    description,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
                TextButton(
                    onClick = {},
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        actionText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = color
                    )
                }
            }
        }
    }
}