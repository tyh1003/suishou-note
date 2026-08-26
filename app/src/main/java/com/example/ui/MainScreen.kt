package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AddEditCardDialog
import com.example.ui.components.ArticleCardItem
import com.example.ui.components.DiaryCardItem
import com.example.ui.components.GeneralCardItem
import com.example.ui.components.ProjectCardItem
import com.example.ui.components.TopCategoryBar
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val editingNote by viewModel.editingNote.collectAsStateWithLifecycle()
    val showAddDialog by viewModel.showAddDialog.collectAsStateWithLifecycle()

    val categoryCounts = remember(notes) {
        mapOf(
            "PROJECT" to notes.count { it.type == "PROJECT" },
            "ARTICLE" to notes.count { it.type == "ARTICLE" },
            "DIARY" to notes.count { it.type == "DIARY" },
            "GENERAL" to notes.count { it.type == "GENERAL" }
        )
    }

    var isSearchExpanded by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("main_screen"),
        containerColor = MinimalBg,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MinimalBg)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 20.dp, top = 20.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "隨手記",
                            fontFamily = FontFamily.Serif,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MinimalPrimary,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "NOTES & IDEAS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MinimalTextMuted,
                            letterSpacing = 2.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = { isSearchExpanded = !isSearchExpanded },
                            modifier = Modifier.testTag("search_toggle_button")
                        ) {
                            Icon(
                                imageVector = if (isSearchExpanded) Icons.Rounded.SearchOff else Icons.Rounded.Search,
                                contentDescription = "搜尋隨手記",
                                tint = MinimalPrimary
                            )
                        }

                        IconButton(
                            onClick = { viewModel.setAllExpandState(true) },
                            modifier = Modifier.testTag("expand_all_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.UnfoldMore,
                                contentDescription = "全部展開",
                                tint = MinimalTextSecondary
                            )
                        }

                        IconButton(
                            onClick = { viewModel.setAllExpandState(false) },
                            modifier = Modifier.testTag("collapse_all_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.UnfoldLess,
                                contentDescription = "全部縮起",
                                tint = MinimalTextSecondary
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = isSearchExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChanged(it) },
                        placeholder = { Text("搜尋靈感、標籤、心情或內容...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = null,
                                tint = MinimalPrimary
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                    Icon(
                                        imageVector = Icons.Rounded.Clear,
                                        contentDescription = "清除搜尋",
                                        tint = MinimalTextMuted
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .testTag("search_text_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White.copy(alpha = 0.85f),
                            focusedBorderColor = MinimalPrimary,
                            unfocusedBorderColor = MinimalBorder
                        )
                    )
                }

                TopCategoryBar(
                    selectedCategory = selectedCategory,
                    categoryCounts = categoryCounts,
                    onCategorySelected = { viewModel.selectCategory(it) }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openAddDialog() },
                containerColor = MinimalFabCoral,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 5.dp),
                modifier = Modifier
                    .size(56.dp)
                    .testTag("main_fab_add")
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "新增字卡",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${selectedCategory.title} (${notes.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MinimalTextSecondary,
                    fontSize = 13.sp
                )

                TextButton(
                    onClick = { viewModel.openAddDialog() },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null,
                        tint = MinimalPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = when (selectedCategory) {
                            NoteCategory.PROJECT -> "新增專案發想"
                            NoteCategory.ARTICLE -> "撰寫文章"
                            NoteCategory.DIARY -> "寫今日日記"
                            NoteCategory.ALL -> "新增字卡"
                        },
                        color = MinimalPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (notes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MinimalSubtleBg,
                            modifier = Modifier.size(80.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.EditNote,
                                contentDescription = null,
                                tint = MinimalPrimary,
                                modifier = Modifier
                                    .padding(20.dp)
                                    .size(40.dp)
                            )
                        }
                        Text(
                            text = if (searchQuery.isBlank()) "尚無字卡紀錄" else "未找到符合「$searchQuery」的字卡",
                            style = MaterialTheme.typography.titleMedium,
                            color = MinimalTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (searchQuery.isBlank()) "點擊下方 ＋ 或右上角按鈕開始記錄" else "請嘗試搜尋其他關鍵字",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MinimalTextSecondary
                        )

                        if (searchQuery.isBlank()) {
                            Button(
                                onClick = { viewModel.openAddDialog() },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MinimalPrimary,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Icon(imageVector = Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("立即建立第一張字卡")
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("note_cards_list"),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 88.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(
                        items = notes,
                        key = { it.id }
                    ) { note ->
                        when (note.type) {
                            "PROJECT" -> ProjectCardItem(
                                note = note,
                                onToggleExpand = { viewModel.toggleExpand(note) },
                                onTogglePin = { viewModel.togglePin(note) },
                                onEdit = { viewModel.openEditDialog(note) },
                                onDuplicate = { viewModel.duplicateNote(note) },
                                onDelete = { viewModel.deleteNote(note.id) }
                            )
                            "ARTICLE" -> ArticleCardItem(
                                note = note,
                                onToggleExpand = { viewModel.toggleExpand(note) },
                                onTogglePin = { viewModel.togglePin(note) },
                                onEdit = { viewModel.openEditDialog(note) },
                                onDuplicate = { viewModel.duplicateNote(note) },
                                onDelete = { viewModel.deleteNote(note.id) }
                            )
                            "DIARY" -> DiaryCardItem(
                                note = note,
                                onToggleExpand = { viewModel.toggleExpand(note) },
                                onTogglePin = { viewModel.togglePin(note) },
                                onEdit = { viewModel.openEditDialog(note) },
                                onDuplicate = { viewModel.duplicateNote(note) },
                                onDelete = { viewModel.deleteNote(note.id) }
                            )
                            else -> GeneralCardItem(
                                note = note,
                                onToggleExpand = { viewModel.toggleExpand(note) },
                                onTogglePin = { viewModel.togglePin(note) },
                                onEdit = { viewModel.openEditDialog(note) },
                                onDuplicate = { viewModel.duplicateNote(note) },
                                onDelete = { viewModel.deleteNote(note.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Card Dialog
    if (showAddDialog) {
        val defaultType = when (selectedCategory) {
            NoteCategory.PROJECT -> "PROJECT"
            NoteCategory.ARTICLE -> "ARTICLE"
            NoteCategory.DIARY -> "DIARY"
            NoteCategory.ALL -> "PROJECT"
        }
        AddEditCardDialog(
            initialNote = null,
            defaultType = defaultType,
            onDismiss = { viewModel.closeAddDialog() },
            onSave = { id, type, title, subtitle, content, dateText ->
                viewModel.saveNote(
                    id = id,
                    type = type,
                    title = title,
                    subtitle = subtitle,
                    content = content,
                    dateText = dateText
                )
                viewModel.closeAddDialog()
            }
        )
    }

    // Edit Note Dialog
    editingNote?.let { note ->
        AddEditCardDialog(
            initialNote = note,
            defaultType = note.type,
            onDismiss = { viewModel.closeEditDialog() },
            onSave = { id, type, title, subtitle, content, dateText ->
                viewModel.saveNote(
                    id = id,
                    type = type,
                    title = title,
                    subtitle = subtitle,
                    content = content,
                    dateText = dateText
                )
                viewModel.closeEditDialog()
            }
        )
    }
}
