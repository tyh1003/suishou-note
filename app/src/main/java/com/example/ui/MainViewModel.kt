package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.NoteCardDao
import com.example.data.NoteCardEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val dao: NoteCardDao = AppDatabase.getDatabase(application).noteCardDao()

    private val _selectedCategory = MutableStateFlow(NoteCategory.PROJECT)
    val selectedCategory: StateFlow<NoteCategory> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Combine room data with category filter and search query
    val notes: StateFlow<List<NoteCardEntity>> = combine(
        dao.getAllNotes(),
        _selectedCategory,
        _searchQuery
    ) { allNotes, category, query ->
        val filteredByCategory = when (category) {
            NoteCategory.PROJECT -> allNotes.filter { it.type == "PROJECT" }
            NoteCategory.ARTICLE -> allNotes.filter { it.type == "ARTICLE" }
            NoteCategory.DIARY -> allNotes.filter { it.type == "DIARY" }
            NoteCategory.ALL -> allNotes
        }

        if (query.isBlank()) {
            filteredByCategory
        } else {
            val q = query.trim().lowercase()
            filteredByCategory.filter { note ->
                note.title.lowercase().contains(q) ||
                note.subtitle.lowercase().contains(q) ||
                note.content.lowercase().contains(q) ||
                note.dateText.lowercase().contains(q)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dialog states
    private val _editingNote = MutableStateFlow<NoteCardEntity?>(null)
    val editingNote: StateFlow<NoteCardEntity?> = _editingNote.asStateFlow()

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()

    init {
        // Clear all previous sample notes from previous seeds so user starts with a clean slate
        viewModelScope.launch {
            val sampleTitles = setOf(
                "個人記帳 App", "AI 創意寫作助手", "智慧盆栽監測系統", "秋日隨想錄", "夜之詩", "週末待採購清單",
                "隨手記 App 開發設計", "個人生活目標管理看板", "午後的陽光與咖啡香"
            )
            val currentNotes = dao.getAllNotes().first()
            currentNotes.forEach { note ->
                if (note.title in sampleTitles || note.title.startsWith("2026-") || note.title.startsWith("2025-")) {
                    dao.deleteNoteById(note.id)
                }
            }
        }
    }

    fun selectCategory(category: NoteCategory) {
        _selectedCategory.value = category
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun toggleExpand(note: NoteCardEntity) {
        viewModelScope.launch {
            dao.updateNote(note.copy(isExpanded = !note.isExpanded, updatedAt = System.currentTimeMillis()))
        }
    }

    fun setAllExpandState(expand: Boolean) {
        viewModelScope.launch {
            dao.setAllExpanded(expand)
        }
    }

    fun togglePin(note: NoteCardEntity) {
        viewModelScope.launch {
            dao.updateNote(note.copy(isPinned = !note.isPinned, updatedAt = System.currentTimeMillis()))
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            dao.deleteNoteById(id)
        }
    }

    fun duplicateNote(note: NoteCardEntity) {
        viewModelScope.launch {
            val duplicated = note.copy(
                id = 0,
                title = "${note.title} (副本)",
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            dao.insertNote(duplicated)
        }
    }

    fun clearAllNotes() {
        viewModelScope.launch {
            dao.deleteAllNotes()
        }
    }

    fun openAddDialog() {
        _showAddDialog.value = true
    }

    fun closeAddDialog() {
        _showAddDialog.value = false
    }

    fun openEditDialog(note: NoteCardEntity) {
        _editingNote.value = note
    }

    fun closeEditDialog() {
        _editingNote.value = null
    }

    fun saveNote(
        id: Long,
        type: String,
        title: String,
        subtitle: String,
        content: String,
        dateText: String
    ) {
        viewModelScope.launch {
            val finalTitle = title.ifBlank {
                when (type) {
                    "DIARY" -> dateText.ifBlank { getTodayDateWithWeekString() }
                    "ARTICLE" -> dateText.ifBlank { getTodayDateString() }
                    "PROJECT" -> "未命名專案"
                    else -> "隨手筆記"
                }
            }
            val finalDateText = dateText.ifBlank {
                if (type == "DIARY") getTodayDateWithWeekString() else getTodayDateString()
            }

            if (id == 0L) {
                // New note
                val newNote = NoteCardEntity(
                    type = type,
                    title = finalTitle,
                    subtitle = subtitle,
                    content = content,
                    dateText = finalDateText,
                    isExpanded = true,
                    isPinned = false,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                dao.insertNote(newNote)
            } else {
                // Update existing
                val existing = dao.getNoteById(id)
                val updatedNote = NoteCardEntity(
                    id = id,
                    type = type,
                    title = finalTitle,
                    subtitle = subtitle,
                    content = content,
                    dateText = finalDateText,
                    isExpanded = existing?.isExpanded ?: true,
                    isPinned = existing?.isPinned ?: false,
                    createdAt = existing?.createdAt ?: System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                dao.updateNote(updatedNote)
            }
        }
    }

    companion object {
        fun getTodayDateString(): String {
            val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            return sdf.format(Date())
        }

        fun getTodayDateWithWeekString(): String {
            val sdf = SimpleDateFormat("yyyy年MM月dd日 EEEE", Locale.TAIWAN)
            return sdf.format(Date())
        }
    }
}
