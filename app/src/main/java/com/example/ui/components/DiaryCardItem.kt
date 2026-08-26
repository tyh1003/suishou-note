package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NoteCardEntity
import com.example.ui.theme.*

@Composable
fun DiaryCardItem(
    note: NoteCardEntity,
    onToggleExpand: () -> Unit,
    onTogglePin: () -> Unit,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (note.isExpanded) 180f else 0f,
        label = "expand_icon_rotation"
    )

    // Mood color and emoji mapping helper
    val moodEmoji = when {
        note.subtitle.contains("開心") || note.subtitle.contains("快樂") -> "😊"
        note.subtitle.contains("難過") || note.subtitle.contains("低落") -> "😢"
        note.subtitle.contains("平靜") || note.subtitle.contains("放鬆") -> "😌"
        note.subtitle.contains("複雜") || note.subtitle.contains("糾結") -> "🤯"
        note.subtitle.contains("活力") || note.subtitle.contains("充實") || note.subtitle.contains("焦慮") -> "⚡"
        note.subtitle.isNotBlank() -> "✨"
        else -> "📝"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("diary_card_${note.id}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = DiarySurface),
        border = BorderStroke(
            width = if (note.isPinned) 1.5.dp else 1.dp,
            color = if (note.isPinned) DiaryPrimary else DiaryBorder
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (note.isPinned) 3.dp else 1.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: 1. Date & Mood tag & Expand / Action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (note.isPinned) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(DiaryContainer)
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PushPin,
                                contentDescription = "已置頂",
                                tint = DiaryPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    // 1. 日期開頭
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CalendarToday,
                            contentDescription = "日期",
                            tint = DiaryPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = note.title.ifBlank { note.dateText.ifBlank { "今日日記" } },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NeutralDark,
                            fontSize = 16.sp
                        )
                    }

                    // 2. 心情及感覺標籤
                    if (note.subtitle.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DiaryContainer,
                            border = BorderStroke(0.6.dp, DiaryBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text(text = moodEmoji, fontSize = 12.sp)
                                Text(
                                    text = note.subtitle,
                                    color = DiaryOnContainer,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // Expand / Collapse Action Button
                    IconButton(
                        onClick = onToggleExpand,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("toggle_expand_diary_${note.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = if (note.isExpanded) "縮起字卡" else "展開字卡",
                            tint = DiaryPrimary,
                            modifier = Modifier.rotate(rotationState)
                        )
                    }

                    // More Menu
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.MoreVert,
                                contentDescription = "更多操作",
                                tint = NeutralMuted
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            shape = RoundedCornerShape(14.dp),
                            containerColor = DiarySurface
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (note.isPinned) "取消置頂" else "置頂字卡") },
                                onClick = {
                                    showMenu = false
                                    onTogglePin()
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.PushPin,
                                        contentDescription = null,
                                        tint = DiaryPrimary
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("編輯日記") },
                                onClick = {
                                    showMenu = false
                                    onEdit()
                                },
                                leadingIcon = {
                                    Icon(Icons.Rounded.Edit, contentDescription = null, tint = DiaryPrimary)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("複製卡片") },
                                onClick = {
                                    showMenu = false
                                    onDuplicate()
                                },
                                leadingIcon = {
                                    Icon(Icons.Rounded.ContentCopy, contentDescription = null, tint = NeutralMuted)
                                }
                            )
                            HorizontalDivider(color = NeutralBorder.copy(alpha = 0.5f))
                            DropdownMenuItem(
                                text = { Text("刪除卡片", color = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    showMenu = false
                                    onDelete()
                                },
                                leadingIcon = {
                                    Icon(Icons.Rounded.DeleteOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                }
                            )
                        }
                    }
                }
            }

            // Collapsed preview excerpt
            if (!note.isExpanded && note.content.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = note.content.replace("\n", " "),
                    style = MaterialTheme.typography.bodySmall,
                    color = NeutralMuted,
                    maxLines = 1,
                    fontSize = 12.sp
                )
            }

            // Expanded Full Content View
            AnimatedVisibility(
                visible = note.isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Detailed journal text box (詳述框)
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFFFFBF8),
                        border = BorderStroke(0.8.dp, NeutralBorder.copy(alpha = 0.6f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (note.content.isBlank()) {
                                Text(
                                    text = "今天過得如何？點擊編輯寫下此刻的心情與感受...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = NeutralMuted,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    fontSize = 14.sp
                                )
                            } else {
                                Text(
                                    text = note.content,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = NeutralDark,
                                    lineHeight = 22.sp,
                                    fontSize = 14.5.sp
                                )
                            }
                        }
                    }

                    // Bottom info strip & edit button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "心情隨筆 • 記錄真實自我",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeutralMuted,
                            fontSize = 11.sp
                        )

                        TextButton(
                            onClick = onEdit,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Edit,
                                contentDescription = null,
                                tint = DiaryPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "編輯日記",
                                color = DiaryPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
