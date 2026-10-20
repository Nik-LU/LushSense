package com.example.iiushka

import com.example.iiushka.data.AppDatabase
import com.example.iiushka.data.ChatDao
import com.example.iiushka.data.ClassifierApi
import com.example.iiushka.data.DefaultChatRepository
import com.example.iiushka.data.buildDatabase
import com.example.iiushka.data.createHttpClient
import com.example.iiushka.domain.ChatRepository
import com.example.iiushka.domain.ClearChatUseCase
import com.example.iiushka.domain.SendMessageUseCase

object AppGraph {
    private val httpClient = createHttpClient()

    private val classifierApi = ClassifierApi(httpClient)

    private val database: AppDatabase by lazy { buildDatabase() }

    val chatDao: ChatDao by lazy { database.chatDao() }

    val chatRepository: ChatRepository by lazy {
        DefaultChatRepository(classifierApi, chatDao)
    }

    val sendMessageUseCase: SendMessageUseCase by lazy {
        SendMessageUseCase(chatRepository)
    }

    val clearChatUseCase: ClearChatUseCase by lazy {
        ClearChatUseCase(chatRepository)
    }
}
