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
fun ArticleCardItem(
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

    // Default title to date if blank
    val displayTitle = note.title.ifBlank { note.dateText.ifBlank { "隨筆文章" } }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("article_card_${note.id}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = ArticleSurface),
        border = BorderStroke(
            width = if (note.isPinned) 1.5.dp else 1.dp,
            color = if (note.isPinned) ArticlePrimary else ArticleBorder
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (note.isPinned) 3.dp else 1.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Title, Tag badge, Expand / Action Menu
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
                                .background(ArticleContainer)
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PushPin,
                                contentDescription = "已置頂",
                                tint = ArticlePrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Text(
                        text = displayTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NeutralDark,
                        fontSize = 17.sp,
                        maxLines = if (note.isExpanded) 2 else 1
                    )

                    // Type tag chip (e.g. 詩, 短文, 小說, 英文, 中文)
                    if (note.subtitle.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ArticleContainer,
                            border = BorderStroke(0.5.dp, ArticleBorder)
                        ) {
                            Text(
                                text = note.subtitle,
                                color = ArticleOnContainer,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                            )
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
                            .testTag("toggle_expand_article_${note.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = if (note.isExpanded) "縮起字卡" else "展開字卡",
                            tint = ArticlePrimary,
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
                            containerColor = ArticleSurface
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
                                        tint = ArticlePrimary
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("編輯文章") },
                                onClick = {
                                    showMenu = false
                                    onEdit()
                                },
                                leadingIcon = {
                                    Icon(Icons.Rounded.Edit, contentDescription = null, tint = ArticlePrimary)
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

            // Expanded View (Full writing canvas)
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
                    // Full text content area
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFFDFCF9),
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
                                    text = "此文章尚無內容，點擊編輯開始創作...",
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
                                    lineHeight = 23.sp,
                                    fontSize = 14.5.sp
                                )
                            }
                        }
                    }

                    // Bottom info strip: word count & date & edit
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "日期: ${note.dateText.ifBlank { "今天" }}",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeutralMuted,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "字數: ${note.content.length} 字",
                                style = MaterialTheme.typography.labelSmall,
                                color = ArticlePrimary,
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp
                            )
                        }

                        TextButton(
                            onClick = onEdit,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Edit,
                                contentDescription = null,
                                tint = ArticlePrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "編輯全文",
                                color = ArticlePrimary,
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
