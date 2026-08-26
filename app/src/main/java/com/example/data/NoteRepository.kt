package com.example.data

import kotlinx.coroutines.flow.Flow

class NoteRepository(private val dao: NoteCardDao) {

    fun getAllNotes(): Flow<List<NoteCardEntity>> = dao.getAllNotes()

    fun getNotesByType(type: String): Flow<List<NoteCardEntity>> = dao.getNotesByType(type)

    fun searchNotes(query: String): Flow<List<NoteCardEntity>> = dao.searchNotes(query)

    fun searchNotesByType(type: String, query: String): Flow<List<NoteCardEntity>> =
        dao.searchNotesByType(type, query)

    suspend fun getNoteById(id: Long): NoteCardEntity? = dao.getNoteById(id)

    suspend fun getNotesCount(): Int = dao.getNotesCount()

    suspend fun insertNote(note: NoteCardEntity): Long = dao.insertNote(note)

    suspend fun updateNote(note: NoteCardEntity) = dao.updateNote(note)

    suspend fun setExpanded(id: Long, isExpanded: Boolean) = dao.setExpanded(id, isExpanded)

    suspend fun setPinned(id: Long, isPinned: Boolean) = dao.setPinned(id, isPinned)

    suspend fun setAllExpandedByType(type: String, isExpanded: Boolean) =
        dao.setAllExpandedByType(type, isExpanded)

    suspend fun deleteNoteById(id: Long) = dao.deleteNoteById(id)
}
