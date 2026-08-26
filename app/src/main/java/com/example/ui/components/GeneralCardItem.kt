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
fun GeneralCardItem(
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

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("general_card_${note.id}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = GeneralSurface),
        border = BorderStroke(
            width = if (note.isPinned) 1.5.dp else 1.dp,
            color = if (note.isPinned) GeneralPrimary else GeneralBorder
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (note.isPinned) 3.dp else 1.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Row 1: 名稱 (Title) & Actions
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
                                .background(GeneralContainer)
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PushPin,
                                contentDescription = "已置頂",
                                tint = GeneralPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Text(
                        text = note.title.ifBlank { "隨手筆記" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NeutralDark,
                        fontSize = 17.sp,
                        maxLines = if (note.isExpanded) 2 else 1
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    IconButton(
                        onClick = onToggleExpand,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("toggle_expand_general_${note.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = if (note.isExpanded) "縮起字卡" else "展開字卡",
                            tint = GeneralPrimary,
                            modifier = Modifier.rotate(rotationState)
                        )
                    }

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
                            containerColor = GeneralSurface
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (note.isPinned) "取消置頂" else "置頂字卡") },
                                onClick = {
                                    showMenu = false
                                    onTogglePin()
                                },
                                leadingIcon = {
                                    Icon(Icons.Rounded.PushPin, contentDescription = null, tint = GeneralPrimary)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("編輯內容") },
                                onClick = {
                                    showMenu = false
                                    onEdit()
                                },
                                leadingIcon = {
                                    Icon(Icons.Rounded.Edit, contentDescription = null, tint = GeneralPrimary)
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

            // Row 2: 日期 (Date)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.CalendarMonth,
                    contentDescription = "日期",
                    tint = NeutralMuted,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = note.dateText.ifBlank { "今天" },
                    style = MaterialTheme.typography.bodySmall,
                    color = NeutralMuted,
                    fontSize = 12.sp
                )
                if (note.subtitle.isNotBlank()) {
                    Text(
                        text = "• ${note.subtitle}",
                        style = MaterialTheme.typography.bodySmall,
                        color = GeneralPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Row 3: 詳述 (Detailed Content)
            AnimatedVisibility(
                visible = note.isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFFDFCFA),
                        border = BorderStroke(0.8.dp, NeutralBorder.copy(alpha = 0.6f))
                    ) {
                        Text(
                            text = note.content.ifBlank { "(尚無詳細內容)" },
                            style = MaterialTheme.typography.bodyMedium,
                            color = NeutralDark,
                            lineHeight = 22.sp,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = onEdit,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Edit,
                                contentDescription = null,
                                tint = GeneralPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "編輯",
                                color = GeneralPrimary,
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
