package com.nbk.insights.ui.composables

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nbk.insights.data.dtos.RecurringPaymentResponse
import com.nbk.insights.ui.theme.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@SuppressLint("DefaultLocale")
@Composable
fun RecurringPaymentCard(
    payment: RecurringPaymentResponse,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick ?: {}
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Icon with background matching transaction item style
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(getColorForCategory(payment.mcc.category).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Repeat,
                        contentDescription = "Recurring payment",
                        tint = getColorForCategory(payment.mcc.category),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = payment.mcc.category,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )

                    // Frequency info instead of subcategory
                    Text(
                        text = getFrequencyText(payment.monthsWithPayments),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Gray500
                    )

                    // Last payment date
                    Text(
                        text = "Last: ${formatDateTime(payment.lastDetected.toString())}",
                        fontSize = 12.sp,
                        color = Gray400
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                // Amount
                Text(
                    text = "-KD ${String.format("%.3f", payment.latestAmount)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Error // Using Error color for debit-like recurring payments
                )

                // Confidence score badge
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = getConfidenceColor(payment.confidenceScore).copy(alpha = 0.1f),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
//                    Text(
//                        text = "${(payment.confidenceScore * 100).toInt()}% confidence",
//                        fontSize = 10.sp,
//                        color = getConfidenceColor(payment.confidenceScore),
//                        fontWeight = FontWeight.Medium,
//                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
//                    )
                }
            }
        }
    }
}

private fun getConfidenceColor(confidenceScore: Double): Color {
    return when {
        confidenceScore >= 0.8 -> Success
        confidenceScore >= 0.6 -> Warning
        else -> Error
    }
}

private fun getFrequencyText(monthsWithPayments: Int): String {
    return when (monthsWithPayments) {
        1 -> "Monthly subscription"
        2 -> "Bi-monthly payment"
        3 -> "Quarterly payment"
        6 -> "Semi-annual payment"
        12 -> "Annual subscription"
        else -> "Every $monthsWithPayments months"
    }
}