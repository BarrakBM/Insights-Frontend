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
import androidx.compose.ui.draw.clip
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