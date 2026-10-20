package com.example.iiushka.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    @Insert
    suspend fun insertMessage(message: MessageEntity): Long

    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC, id ASC")
    fun observeMessages(): Flow<List<MessageEntity>>

    @Query("DELETE FROM chat_messages")
    suspend fun clearChat()
}
