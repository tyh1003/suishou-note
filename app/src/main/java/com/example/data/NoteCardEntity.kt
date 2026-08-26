package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_cards")
data class NoteCardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // "PROJECT", "ARTICLE", "DIARY", "GENERAL"
    val title: String,
    val subtitle: String = "", // e.g. 方向/工具, 標籤 (詩/短文), 心情 (開心/複雜)
    val content: String = "", // 詳細內容 / 想法 / 日記
    val dateText: String = "", // e.g. "2026-08-25" or "2026/08/25 週二"
    val extraTag: String = "", // e.g. 自訂標籤或狀態
    val isPinned: Boolean = false,
    val isExpanded: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
