package com.nbk.insights.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nbk.insights.data.dtos.Account
import com.nbk.insights.data.dtos.AccountType
import com.nbk.insights.data.dtos.CategoryRecommendationResponse
import com.nbk.insights.ui.theme.TextPrimary
import java.math.BigDecimal

@Composable
fun SmartRecommendationsDrawer(recommendations: List<CategoryRecommendationResponse>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            "Smart Recommendations",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )

        recommendations.forEach {
            CategoryRecommendationDrawer(
                category = it.category,
                recommendation = it.recommendation
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFAFAFA)
@Composable
fun SmartRecommendationsDrawerPreview() {
    val mockRecommendations = listOf(
        CategoryRecommendationResponse(
            category = "DINING",
            recommendation = "You're 100% under budget. Great job on controlling your dining expenses. Consider exploring NBK’s dining offers to enjoy meals at great prices."
        ),
        CategoryRecommendationResponse(
            category = "TRANSPORT",
            recommendation = "You’ve spent less on fuel this month. Keep this up by carpooling or using public transport."
        ),
        CategoryRecommendationResponse(
            category = "GROCERIES",
            recommendation = "Grocery spending increased 15% this month. Consider meal planning and using loyalty programs."
        )
    )

    SmartRecommendationsDrawer(recommendations = mockRecommendations)
}

