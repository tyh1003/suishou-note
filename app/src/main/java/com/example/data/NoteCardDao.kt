package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteCardDao {
    @Query("SELECT * FROM note_cards ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteCardEntity>>

    @Query("SELECT * FROM note_cards WHERE type = :type ORDER BY isPinned DESC, updatedAt DESC")
    fun getNotesByType(type: String): Flow<List<NoteCardEntity>>

    @Query("SELECT * FROM note_cards WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' OR subtitle LIKE '%' || :query || '%' ORDER BY isPinned DESC, updatedAt DESC")
    fun searchNotes(query: String): Flow<List<NoteCardEntity>>

    @Query("SELECT * FROM note_cards WHERE type = :type AND (title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' OR subtitle LIKE '%' || :query || '%') ORDER BY isPinned DESC, updatedAt DESC")
    fun searchNotesByType(type: String, query: String): Flow<List<NoteCardEntity>>

    @Query("SELECT * FROM note_cards WHERE id = :id LIMIT 1")
    suspend fun getNoteById(id: Long): NoteCardEntity?

    @Query("SELECT COUNT(*) FROM note_cards")
    suspend fun getNotesCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteCardEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notes: List<NoteCardEntity>)

    @Update
    suspend fun updateNote(note: NoteCardEntity)

    @Query("UPDATE note_cards SET isExpanded = :isExpanded WHERE id = :id")
    suspend fun setExpanded(id: Long, isExpanded: Boolean)

    @Query("UPDATE note_cards SET isPinned = :isPinned WHERE id = :id")
    suspend fun setPinned(id: Long, isPinned: Boolean)

    @Query("UPDATE note_cards SET isExpanded = :isExpanded")
    suspend fun setAllExpanded(isExpanded: Boolean)

    @Query("UPDATE note_cards SET isExpanded = :isExpanded WHERE type = :type")
    suspend fun setAllExpandedByType(type: String, isExpanded: Boolean)

    @Query("DELETE FROM note_cards WHERE id = :id")
    suspend fun deleteNoteById(id: Long)

    @Query("DELETE FROM note_cards")
    suspend fun deleteAllNotes()
}
