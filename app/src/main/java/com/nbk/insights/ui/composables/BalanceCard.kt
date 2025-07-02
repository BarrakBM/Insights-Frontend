package com.nbk.insights.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nbk.insights.ui.theme.NBKBlue

@Composable
fun BalanceCard(
    balance: String,
    isBalanceVisible: Boolean,
    onToggleVisibility: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 40.dp)
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp)), // Reduced shadow to match AccountCard
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            NBKBlue.copy(red = NBKBlue.red * 1.15f, green = NBKBlue.green * 1.15f, blue = NBKBlue.blue * 1.1f), // Lighter start
                            NBKBlue,
                            NBKBlue.copy(red = NBKBlue.red * 0.85f, green = NBKBlue.green * 0.85f, blue = NBKBlue.blue * 0.9f), // More contrast
                            NBKBlue.copy(red = NBKBlue.red * 0.7f, green = NBKBlue.green * 0.7f, blue = NBKBlue.blue * 0.8f) // Darker end
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY) // Diagonal gradient
                    )
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Total Balance",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f) // Increased opacity to match
                )
                Text(
                    text = if (isBalanceVisible) balance else "**************",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.CenterEnd)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f)) // Match AccountCard icon background
                    .clickable { onToggleVisibility() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isBalanceVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = "Toggle visibility",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}