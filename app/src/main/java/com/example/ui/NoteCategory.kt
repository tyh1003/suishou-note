package com.example.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Article
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.*

enum class NoteCategory(
    val typeKey: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val bgTone: Color,
    val primaryColor: Color,
    val containerColor: Color,
    val borderColor: Color
) {
    PROJECT(
        typeKey = "PROJECT",
        title = "專案發想",
        subtitle = "開發與工具規劃",
        icon = Icons.Rounded.Lightbulb,
        bgTone = WarmClayBase,
        primaryColor = ProjectPrimary,
        containerColor = ProjectContainer,
        borderColor = ProjectBorder
    ),
    ARTICLE(
        typeKey = "ARTICLE",
        title = "文章類型",
        subtitle = "創作、詩文與隨筆",
        icon = Icons.Rounded.Article,
        bgTone = WarmIvoryBase,
        primaryColor = ArticlePrimary,
        containerColor = ArticleContainer,
        borderColor = ArticleBorder
    ),
    DIARY(
        typeKey = "DIARY",
        title = "日記類型",
        subtitle = "心情與每日記錄",
        icon = Icons.Rounded.Book,
        bgTone = WarmPeachBase,
        primaryColor = DiaryPrimary,
        containerColor = DiaryContainer,
        borderColor = DiaryBorder
    ),
    FREE(
        typeKey = "FREE",
        title = "任意",
        subtitle = "自由記事與備忘",
        icon = Icons.Rounded.EditNote,
        bgTone = WarmSandBase,
        primaryColor = FreePrimary,
        containerColor = FreeContainer,
        borderColor = FreeBorder
    ),
    ALL(
        typeKey = "ALL",
        title = "全部記事",
        subtitle = "所有隨手記錄",
        icon = Icons.Rounded.Dashboard,
        bgTone = WarmSandBase,
        primaryColor = GeneralPrimary,
        containerColor = GeneralContainer,
        borderColor = GeneralBorder
    );

    val displayName: String get() = title
}
