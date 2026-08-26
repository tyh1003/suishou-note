package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.NoteCategory
import com.example.ui.theme.NeutralDark
import com.example.ui.theme.NeutralMuted

@Composable
fun TopCategoryBar(
    selectedCategory: NoteCategory,
    categoryCounts: Map<String, Int>,
    onCategorySelected: (NoteCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        NoteCategory.values().forEach { category ->
            val isSelected = selectedCategory == category
            val count = if (category == NoteCategory.ALL) {
                categoryCounts.values.sum()
            } else {
                categoryCounts[category.typeKey] ?: 0
            }

            val bgColor by animateColorAsState(
                targetValue = if (isSelected) category.primaryColor else Color.White.copy(alpha = 0.75f),
                animationSpec = tween(durationMillis = 200),
                label = "pill_bg"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else NeutralDark,
                animationSpec = tween(durationMillis = 200),
                label = "pill_text"
            )
            val badgeBgColor by animateColorAsState(
                targetValue = if (isSelected) Color.White.copy(alpha = 0.25f) else category.containerColor,
                animationSpec = tween(durationMillis = 200),
                label = "badge_bg"
            )
            val badgeTextColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else category.primaryColor,
                animationSpec = tween(durationMillis = 200),
                label = "badge_text"
            )

            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onCategorySelected(category) }
                    .testTag("tab_${category.name.lowercase()}"),
                shape = RoundedCornerShape(20.dp),
                color = bgColor,
                shadowElevation = if (isSelected) 3.dp else 1.dp,
                border = if (!isSelected) ButtonDefaults.outlinedButtonBorder else null
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = category.title,
                        tint = textColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = category.title,
                        color = textColor,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )

                    // Count badge
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(badgeBgColor)
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = count.toString(),
                            color = badgeTextColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
