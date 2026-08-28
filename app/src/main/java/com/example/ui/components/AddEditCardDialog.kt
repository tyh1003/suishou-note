package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.NoteCardEntity
import com.example.ui.MainViewModel
import com.example.ui.NoteCategory
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCardDialog(
    initialNote: NoteCardEntity? = null,
    defaultType: String = "PROJECT",
    onDismiss: () -> Unit,
    onSave: (
        id: Long,
        type: String,
        title: String,
        subtitle: String,
        content: String,
        dateText: String
    ) -> Unit
) {
    var selectedType by remember {
        mutableStateOf(initialNote?.type ?: defaultType)
    }
    val todayDate = remember { MainViewModel.getTodayDateString() }
    val todayDateWithWeek = remember { MainViewModel.getTodayDateWithWeekString() }

    var title by remember { mutableStateOf(initialNote?.title ?: "") }
    var subtitle by remember { mutableStateOf(initialNote?.subtitle ?: "") }

    // Initial content and cursor positioning
    val initialContentStr = remember {
        initialNote?.content ?: if (selectedType == "PROJECT") "• " else ""
    }
    var contentTextFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = initialContentStr,
                selection = TextRange(initialContentStr.length)
            )
        )
    }

    var dateText by remember {
        mutableStateOf(
            initialNote?.dateText ?: if (selectedType == "DIARY") todayDateWithWeek else todayDate
        )
    }

    // 2. 文章五個選項：詩、短文、小說、英文、中文 (支援多選/複選)
    val articleGenres = remember { listOf("詩", "短文", "小說", "英文", "中文") }
    val initialSelectedGenres = remember {
        val raw = initialNote?.subtitle ?: ""
        val matched = mutableSetOf<String>()
        articleGenres.forEach { genre ->
            if (raw.contains(genre)) {
                matched.add(genre)
            }
        }
        matched
    }
    var selectedGenres by remember { mutableStateOf<Set<String>>(initialSelectedGenres) }

    // Custom tag input for article
    val initialCustomTag = remember {
        val raw = initialNote?.subtitle ?: ""
        var remaining = raw
        articleGenres.forEach { genre ->
            remaining = remaining.replace(genre, "")
        }
        remaining.replace("/", "").replace(",", "").replace("、", "").trim()
    }
    var customTagInput by remember { mutableStateOf(initialCustomTag) }
    var showCustomTagField by remember { mutableStateOf(initialCustomTag.isNotBlank()) }

    // Diary Moods
    val diaryMoods = remember {
        listOf(
            "開心" to "😊",
            "難過" to "😢",
            "平靜放鬆" to "😌",
            "複雜" to "🤯",
            "充滿活力" to "⚡"
        )
    }
    val initialCustomMood = remember {
        val raw = initialNote?.subtitle ?: ""
        if (raw.isNotBlank() && diaryMoods.none { it.first == raw }) raw else ""
    }
    var customMoodInput by remember { mutableStateOf(initialCustomMood) }
    var showCustomMoodField by remember { mutableStateOf(initialCustomMood.isNotBlank()) }

    // Function to sync article subtitle string from selected multi-chips
    fun syncArticleSubtitle(genres: Set<String>, customTag: String, customEnabled: Boolean) {
        val sorted = articleGenres.filter { it in genres }
        val all = if (customEnabled && customTag.isNotBlank()) {
            sorted + listOf(customTag.trim())
        } else {
            sorted
        }
        subtitle = all.joinToString(" / ")
    }

    // Palette per type
    val category = when (selectedType) {
        "PROJECT" -> NoteCategory.PROJECT
        "ARTICLE" -> NoteCategory.ARTICLE
        "DIARY" -> NoteCategory.DIARY
        "FREE" -> NoteCategory.FREE
        else -> NoteCategory.ALL
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.90f)
                .testTag("add_edit_dialog"),
            shape = RoundedCornerShape(26.dp),
            color = MinimalBg,
            border = BorderStroke(1.2.dp, category.borderColor),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header with icon and title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(category.primaryColor.copy(alpha = 0.12f))
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = when (selectedType) {
                                    "PROJECT" -> Icons.Rounded.Code
                                    "ARTICLE" -> Icons.Rounded.Article
                                    "DIARY" -> Icons.Rounded.Favorite
                                    "FREE" -> Icons.Rounded.EditNote
                                    else -> Icons.Rounded.DashboardCustomize
                                },
                                contentDescription = null,
                                tint = category.primaryColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Text(
                            text = if (initialNote != null) {
                                "編輯「${category.displayName}」字卡"
                            } else {
                                "新增「${category.displayName}」字卡"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MinimalTextPrimary
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "關閉",
                            tint = MinimalTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Mode switcher when creating new card
                if (initialNote == null) {
                    Text(
                        text = "切換類型",
                        style = MaterialTheme.typography.labelSmall,
                        color = MinimalTextSecondary,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Triple("PROJECT", "專案發想", Icons.Rounded.Code),
                            Triple("ARTICLE", "文章 (詩/文/小說)", Icons.Rounded.Article),
                            Triple("DIARY", "生活日記", Icons.Rounded.Favorite),
                            Triple("FREE", "任意", Icons.Rounded.EditNote)
                        ).forEach { (typeKey, typeLabel, icon) ->
                            val isSelected = selectedType == typeKey
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedType = typeKey
                                    if (typeKey == "PROJECT" && contentTextFieldValue.text.isEmpty()) {
                                        contentTextFieldValue = TextFieldValue("• ", TextRange(2))
                                    } else if (typeKey == "DIARY") {
                                        dateText = todayDateWithWeek
                                    } else if (typeKey == "ARTICLE") {
                                        dateText = todayDate
                                    } else if (typeKey == "FREE") {
                                        dateText = todayDate
                                    }
                                },
                                label = { Text(typeLabel, fontSize = 12.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp)
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = category.primaryColor,
                                    selectedLabelColor = Color.White,
                                    selectedLeadingIconColor = Color.White,
                                    containerColor = Color.White,
                                    labelColor = MinimalTextPrimary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Scrollable Form Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    when (selectedType) {
                        "PROJECT" -> {
                            // 1. 開發/專案發想 (PROJECT)
                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                label = { Text("專案名稱") },
                                placeholder = { Text("例如：隨手記 App、智能助理規劃") },
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("project_title_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White.copy(alpha = 0.9f),
                                    focusedBorderColor = ProjectPrimary,
                                    unfocusedBorderColor = ProjectBorder
                                )
                            )

                            // 簡述框 (方向、工具)
                            OutlinedTextField(
                                value = subtitle,
                                onValueChange = { subtitle = it },
                                label = { Text("簡述 (方向、工具)") },
                                placeholder = { Text("例如：方向: 離線極簡 | 工具: Compose, Room") },
                                singleLine = false,
                                maxLines = 2,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("project_subtitle_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White.copy(alpha = 0.9f),
                                    focusedBorderColor = ProjectPrimary,
                                    unfocusedBorderColor = ProjectBorder
                                )
                            )

                            // 詳述框 (開發想法，列點式寫法)
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "詳述 (想法 • 支援換行自動列點)",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = ProjectPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    // Quick insert bullet button with auto cursor positioning behind bullet
                                    Button(
                                        onClick = {
                                            contentTextFieldValue = insertBulletPoint(contentTextFieldValue)
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = ProjectContainer,
                                            contentColor = ProjectPrimary
                                        ),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Add,
                                            contentDescription = null,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("插入列點 (•)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                OutlinedTextField(
                                    value = contentTextFieldValue,
                                    onValueChange = { newValue ->
                                        contentTextFieldValue = handleBulletContentChange(contentTextFieldValue, newValue)
                                    },
                                    placeholder = { Text("• 第一點想法與規劃\n• 第二點技術選型\n• 第三點排程與進度") },
                                    minLines = 6,
                                    maxLines = 14,
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("project_content_input"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White.copy(alpha = 0.9f),
                                        focusedBorderColor = ProjectPrimary,
                                        unfocusedBorderColor = ProjectBorder
                                    )
                                )
                            }
                        }

                        "ARTICLE" -> {
                            // 2. 文章類型 (ARTICLE)
                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                label = { Text("文章名稱 (留空將自動填入今日日期)") },
                                placeholder = { Text("留空自動填入: $todayDate") },
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("article_title_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White.copy(alpha = 0.9f),
                                    focusedBorderColor = ArticlePrimary,
                                    unfocusedBorderColor = ArticleBorder
                                )
                            )

                            // 簡述框 (詩、短文、小說、英文、中文 五個選項支援多選)
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "文章標籤 (五選項支援多選/複選)",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = ArticlePrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    if (subtitle.isNotBlank()) {
                                        Text(
                                            text = "已選: $subtitle",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MinimalTextSecondary,
                                            maxLines = 1
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    articleGenres.forEach { genre ->
                                        val isSelected = genre in selectedGenres
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = {
                                                selectedGenres = if (isSelected) {
                                                    selectedGenres - genre
                                                } else {
                                                    selectedGenres + genre
                                                }
                                                syncArticleSubtitle(selectedGenres, customTagInput, showCustomTagField)
                                            },
                                            leadingIcon = if (isSelected) {
                                                {
                                                    Icon(
                                                        imageVector = Icons.Rounded.Check,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(15.dp),
                                                        tint = Color.White
                                                    )
                                                }
                                            } else null,
                                            label = {
                                                Text(
                                                    text = genre,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                )
                                            },
                                            shape = RoundedCornerShape(12.dp),
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = ArticlePrimary,
                                                selectedLabelColor = Color.White,
                                                selectedLeadingIconColor = Color.White,
                                                containerColor = Color.White,
                                                labelColor = MinimalTextPrimary
                                            )
                                        )
                                    }

                                    // 其他自填按鈕
                                    FilterChip(
                                        selected = showCustomTagField,
                                        onClick = {
                                            showCustomTagField = !showCustomTagField
                                            if (!showCustomTagField) {
                                                customTagInput = ""
                                            }
                                            syncArticleSubtitle(selectedGenres, customTagInput, showCustomTagField)
                                        },
                                        leadingIcon = if (showCustomTagField) {
                                            {
                                                Icon(
                                                    imageVector = Icons.Rounded.Check,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(15.dp),
                                                    tint = Color.White
                                                )
                                            }
                                        } else null,
                                        label = { Text("✏️ 其他自填") },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = ArticlePrimary,
                                            selectedLabelColor = Color.White,
                                            selectedLeadingIconColor = Color.White,
                                            containerColor = Color.White,
                                            labelColor = MinimalTextPrimary
                                        )
                                    )
                                }

                                if (showCustomTagField) {
                                    OutlinedTextField(
                                        value = customTagInput,
                                        onValueChange = {
                                            customTagInput = it
                                            syncArticleSubtitle(selectedGenres, it, showCustomTagField)
                                        },
                                        label = { Text("自訂文章標籤") },
                                        placeholder = { Text("例如：讀書筆記、隨想錄、劇本") },
                                        singleLine = true,
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = Color.White,
                                            unfocusedContainerColor = Color.White,
                                            focusedBorderColor = ArticlePrimary,
                                            unfocusedBorderColor = ArticleBorder
                                        )
                                    )
                                }
                            }

                            // 詳述 (寫文章內容，不限字數)
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "詳述 (寫作內容 • 不限字數)",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = ArticlePrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Button(
                                        onClick = {
                                            contentTextFieldValue = insertBulletPoint(contentTextFieldValue)
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = ArticleContainer,
                                            contentColor = ArticlePrimary
                                        ),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Add,
                                            contentDescription = null,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("插入列點 (•)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                OutlinedTextField(
                                    value = contentTextFieldValue,
                                    onValueChange = { newValue ->
                                        contentTextFieldValue = handleBulletContentChange(contentTextFieldValue, newValue)
                                    },
                                    placeholder = { Text("寫下詩篇、短文、小說或隨心紀錄...") },
                                    minLines = 7,
                                    maxLines = 16,
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("article_content_input"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White.copy(alpha = 0.9f),
                                        focusedBorderColor = ArticlePrimary,
                                        unfocusedBorderColor = ArticleBorder
                                    )
                                )
                            }
                        }

                        "DIARY" -> {
                            // 3. 日記類型 (DIARY)
                            OutlinedTextField(
                                value = dateText,
                                onValueChange = {
                                    dateText = it
                                    title = it
                                },
                                label = { Text("日記日期 (固定為年月日 + 星期幾)") },
                                placeholder = { Text(todayDateWithWeek) },
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("diary_date_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White.copy(alpha = 0.9f),
                                    focusedBorderColor = DiaryPrimary,
                                    unfocusedBorderColor = DiaryBorder
                                )
                            )

                            // 簡述框 (心情: 開心、難過、平靜放鬆、複雜、充滿活力、其他自填)
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "今日心情",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = DiaryPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    diaryMoods.forEach { (moodName, emoji) ->
                                        val isSelected = subtitle == moodName && !showCustomMoodField
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = {
                                                subtitle = moodName
                                                showCustomMoodField = false
                                            },
                                            label = { Text("$emoji $moodName") },
                                            shape = RoundedCornerShape(12.dp),
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = DiaryPrimary,
                                                selectedLabelColor = Color.White,
                                                containerColor = Color.White,
                                                labelColor = MinimalTextPrimary
                                            )
                                        )
                                    }

                                    FilterChip(
                                        selected = showCustomMoodField,
                                        onClick = {
                                            showCustomMoodField = !showCustomMoodField
                                            if (showCustomMoodField && customMoodInput.isNotBlank()) {
                                                subtitle = customMoodInput
                                            }
                                        },
                                        label = { Text("✏️ 其他自填") },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = DiaryPrimary,
                                            selectedLabelColor = Color.White,
                                            containerColor = Color.White,
                                            labelColor = MinimalTextPrimary
                                        )
                                    )
                                }

                                if (showCustomMoodField) {
                                    OutlinedTextField(
                                        value = customMoodInput,
                                        onValueChange = {
                                            customMoodInput = it
                                            subtitle = it
                                        },
                                        label = { Text("自訂心情感受") },
                                        placeholder = { Text("例如：感激、充滿期待、有點疲倦") },
                                        singleLine = true,
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = Color.White,
                                            unfocusedContainerColor = Color.White,
                                            focusedBorderColor = DiaryPrimary,
                                            unfocusedBorderColor = DiaryBorder
                                        )
                                    )
                                }
                            }

                            // 詳述 (日記內容)
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "詳述 (日記內容 • 不限字數)",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = DiaryPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Button(
                                        onClick = {
                                            contentTextFieldValue = insertBulletPoint(contentTextFieldValue)
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = DiaryContainer,
                                            contentColor = DiaryPrimary
                                        ),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Add,
                                            contentDescription = null,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("插入列點 (•)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                OutlinedTextField(
                                    value = contentTextFieldValue,
                                    onValueChange = { newValue ->
                                        contentTextFieldValue = handleBulletContentChange(contentTextFieldValue, newValue)
                                    },
                                    placeholder = { Text("記錄今天的生活點滴、反思與心情...") },
                                    minLines = 7,
                                    maxLines = 16,
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("diary_content_input"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White.copy(alpha = 0.9f),
                                        focusedBorderColor = DiaryPrimary,
                                        unfocusedBorderColor = DiaryBorder
                                    )
                                )
                            }
                        }

                        "FREE" -> {
                            // 4. 任意 (FREE)
                            // 檔名
                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                label = { Text("檔名") },
                                placeholder = { Text("例如：會議記錄、臨時備忘、靈感草稿") },
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("free_title_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White.copy(alpha = 0.9f),
                                    focusedBorderColor = FreePrimary,
                                    unfocusedBorderColor = FreeBorder
                                )
                            )

                            // 日期
                            OutlinedTextField(
                                value = dateText,
                                onValueChange = { dateText = it },
                                label = { Text("日期") },
                                placeholder = { Text(todayDate) },
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("free_date_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White.copy(alpha = 0.9f),
                                    focusedBorderColor = FreePrimary,
                                    unfocusedBorderColor = FreeBorder
                                )
                            )

                            // 詳述記事框 (支援換行自動列點)
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "詳述 (記事內容 • 支援換行自動列點)",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = FreePrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Button(
                                        onClick = {
                                            contentTextFieldValue = insertBulletPoint(contentTextFieldValue)
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = FreeContainer,
                                            contentColor = FreePrimary
                                        ),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Add,
                                            contentDescription = null,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("插入列點 (•)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                OutlinedTextField(
                                    value = contentTextFieldValue,
                                    onValueChange = { newValue ->
                                        contentTextFieldValue = handleBulletContentChange(contentTextFieldValue, newValue)
                                    },
                                    placeholder = { Text("記錄任何想法、待辦事項、自由筆記...") },
                                    minLines = 7,
                                    maxLines = 16,
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("free_content_input"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White.copy(alpha = 0.9f),
                                        focusedBorderColor = FreePrimary,
                                        unfocusedBorderColor = FreeBorder
                                    )
                                )
                            }
                        }

                        else -> {
                            // GENERAL fallback
                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                label = { Text("字卡名稱") },
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = subtitle,
                                onValueChange = { subtitle = it },
                                label = { Text("簡述") },
                                singleLine = false,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = contentTextFieldValue,
                                onValueChange = { newValue ->
                                    contentTextFieldValue = handleBulletContentChange(contentTextFieldValue, newValue)
                                },
                                label = { Text("詳述") },
                                minLines = 6,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Bottom action bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("dialog_cancel_button")
                    ) {
                        Text("取消", color = MinimalTextSecondary)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val finalContent = contentTextFieldValue.text
                            onSave(
                                initialNote?.id ?: 0L,
                                selectedType,
                                title.trim(),
                                subtitle.trim(),
                                finalContent,
                                dateText.trim()
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = category.primaryColor,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.testTag("dialog_save_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (initialNote != null) "儲存變更" else "建立字卡",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Handles smart newline bullet insertion and empty-bullet line deletion when Enter is pressed.
 */
private fun handleBulletContentChange(
    oldValue: TextFieldValue,
    newValue: TextFieldValue
): TextFieldValue {
    val oldText = oldValue.text
    val newText = newValue.text
    val newCursor = newValue.selection.start

    // Check if the user pressed Enter (text length increased by 1 and char at cursor - 1 is '\n')
    if (newText.length == oldText.length + 1 && newCursor > 0 && newText[newCursor - 1] == '\n') {
        val lastNewlineIndex = newText.lastIndexOf('\n', newCursor - 2)
        val prevLine = if (lastNewlineIndex == -1) {
            newText.substring(0, newCursor - 1)
        } else {
            newText.substring(lastNewlineIndex + 1, newCursor - 1)
        }

        val trimmedPrev = prevLine.trim()
        if (trimmedPrev == "•") {
            // User pressed Enter on an empty bullet line -> exit bullet mode!
            val beforeLineStart = if (lastNewlineIndex == -1) "" else newText.substring(0, lastNewlineIndex + 1)
            val afterCursor = newText.substring(newCursor)
            val resultText = beforeLineStart + afterCursor
            val resultCursor = beforeLineStart.length
            return TextFieldValue(resultText, TextRange(resultCursor))
        } else if (prevLine.startsWith("• ") || prevLine.startsWith("•")) {
            // User pressed Enter after a bullet line -> auto-insert next bullet '• ' and place cursor right after
            val beforeCursor = newText.substring(0, newCursor)
            val afterCursor = newText.substring(newCursor)
            val bulletPrefix = "• "
            val resultText = beforeCursor + bulletPrefix + afterCursor
            val resultCursor = newCursor + bulletPrefix.length
            return TextFieldValue(resultText, TextRange(resultCursor))
        }
    }

    return newValue
}

/**
 * Inserts a bullet point '• ' at cursor position and sets the cursor directly after the space.
 */
private fun insertBulletPoint(current: TextFieldValue): TextFieldValue {
    val text = current.text
    val selection = current.selection
    val start = selection.start
    val end = selection.end

    val before = text.substring(0, start)
    val after = text.substring(end)

    val bullet = if (before.isEmpty() || before.endsWith("\n")) {
        "• "
    } else {
        "\n• "
    }

    val newText = before + bullet + after
    val newPos = start + bullet.length
    return TextFieldValue(newText, TextRange(newPos))
}
