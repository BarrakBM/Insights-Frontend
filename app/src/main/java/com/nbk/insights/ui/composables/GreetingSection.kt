package com.nbk.insights.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nbk.insights.ui.theme.TextPrimary
import com.nbk.insights.ui.theme.TextSecondary


@Composable
fun GreetingSection(firstName: String) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            "Hello, $firstName",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            "Here's your financial overview for today",
            fontSize = 14.sp,
            color = TextSecondary
        )
    }
}