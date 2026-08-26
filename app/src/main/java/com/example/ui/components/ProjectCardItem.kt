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
fun ProjectCardItem(
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
            .testTag("project_card_${note.id}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = ProjectSurface),
        border = BorderStroke(
            width = if (note.isPinned) 1.5.dp else 1.dp,
            color = if (note.isPinned) ProjectPrimary else ProjectBorder
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (note.isPinned) 3.dp else 1.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Title & Expand / Action Buttons
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
                                .background(ProjectContainer)
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PushPin,
                                contentDescription = "已置頂",
                                tint = ProjectPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Text(
                        text = note.title.ifBlank { "未命名專案發想" },
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
                    // Expand / Collapse Action Button
                    IconButton(
                        onClick = onToggleExpand,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("toggle_expand_${note.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = if (note.isExpanded) "縮起字卡" else "展開字卡",
                            tint = ProjectPrimary,
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
                            containerColor = ProjectSurface
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (note.isPinned) "取消置頂" else "置頂字卡") },
                                onClick = {
                                    showMenu = false
                                    onTogglePin()
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (note.isPinned) Icons.Rounded.PushPin else Icons.Rounded.PushPin,
                                        contentDescription = null,
                                        tint = ProjectPrimary
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("編輯內容") },
                                onClick = {
                                    showMenu = false
                                    onEdit()
                                },
                                leadingIcon = {
                                    Icon(Icons.Rounded.Edit, contentDescription = null, tint = ProjectPrimary)
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

            // Collapsed preview info
            if (!note.isExpanded && note.subtitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = note.subtitle,
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
                    // 1. 簡述框 (方向、工具)
                    if (note.subtitle.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = ProjectContainer.copy(alpha = 0.6f),
                            border = BorderStroke(0.8.dp, ProjectBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.BuildCircle,
                                    contentDescription = "方向與工具",
                                    tint = ProjectPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = note.subtitle,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = ProjectOnContainer,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    // 2. 詳述框 (開發想法 / 列點式寫法)
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFFDFBF7),
                        border = BorderStroke(0.8.dp, NeutralBorder.copy(alpha = 0.7f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.FormatListBulleted,
                                    contentDescription = "列點想法",
                                    tint = ProjectPrimary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "開發想法與規劃 (列點清單)",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = ProjectPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            HorizontalDivider(color = NeutralBorder.copy(alpha = 0.4f))

                            if (note.content.isBlank()) {
                                Text(
                                    text = "尚無詳細想法，點擊右上方選單或下方編輯進行記錄...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = NeutralMuted,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    fontSize = 13.sp
                                )
                            } else {
                                // Split lines for clean bullet presentation
                                val lines = note.content.split("\n")
                                lines.forEach { line ->
                                    val trimmed = line.trim()
                                    if (trimmed.isNotEmpty()) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            if (!trimmed.startsWith("•") && !trimmed.startsWith("-")) {
                                                Text(
                                                    text = "•",
                                                    color = ProjectPrimary,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp
                                                )
                                            }
                                            Text(
                                                text = trimmed,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = NeutralDark,
                                                lineHeight = 20.sp,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Bottom Quick Edit Strip & Date
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "建立日期: ${note.dateText.ifBlank { "今天" }}",
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
                                imageVector = Icons.Rounded.EditNote,
                                contentDescription = null,
                                tint = ProjectPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "快速修改",
                                color = ProjectPrimary,
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
