package com.nbk.insights.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nbk.insights.data.dtos.BankCardDTO
import com.nbk.insights.ui.theme.InsightsTheme

@Composable
fun CardBarItemWithDropdown(
    card: BankCardDTO,
    onActionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Card Icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF3F4F6)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CreditCard,
                        contentDescription = "Card",
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = card.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Text(
                        text = "•••${card.lastFourDigits} | ${card.type}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = card.balance.replace("KD ", "$"),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))

                // Dropdown Menu Button
                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.BarChart,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = Color(0xFF1E3A8A)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("View Insights")
                                }
                            },
                            onClick = {
                                expanded = false
                                onActionSelected("View Insights")
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.TrendingUp,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = Color(0xFF1E3A8A)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Start Budgeting")
                                }
                            },
                            onClick = {
                                expanded = false
                                onActionSelected("Start Budgeting")
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.List,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = Color(0xFF1E3A8A)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("View All Transactions")
                                }
                            },
                            onClick = {
                                expanded = false
                                onActionSelected("View All Transactions")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CardBarItemWithDropdownPreview() {
    InsightsTheme {
        CardBarItemWithDropdown(
            card = BankCardDTO(
                type = "Debit Card",
                name = "NBK Titanium",
                lastFourDigits = "5678",
                balance = "KD 3,456.78",
                expiryDate = "05/26"
            ),
            onActionSelected = { }
        )
    }
}