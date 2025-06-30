package com.nbk.insights.ui.composables

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nbk.insights.ui.theme.TextPrimary
import com.nbk.insights.ui.theme.TextSecondary

@Composable
fun CategoryRecommendationDrawer(
    category: String,
    recommendation: String
) {
    var expanded by remember { mutableStateOf(false) }

    val transition = updateTransition(targetState = expanded, label = "DrawerTransition")

    val cardElevation by transition.animateDp(label = "Elevation") {
        if (it) 8.dp else 2.dp
    }

    val cardColor by transition.animateColor(label = "Color") {
        if (it) Color(0xFFF5F5F5) else Color.White
    }

    val iconRotation by transition.animateFloat(label = "Rotation") {
        if (it) 180f else 0f
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(cardElevation, RoundedCornerShape(8.dp))
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = category.replaceFirstChar { it.uppercase() },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = null,
                    modifier = Modifier.rotate(iconRotation),
                    tint = TextSecondary
                )
            }

            if (expanded) {
                Text(
                    text = recommendation,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }
}