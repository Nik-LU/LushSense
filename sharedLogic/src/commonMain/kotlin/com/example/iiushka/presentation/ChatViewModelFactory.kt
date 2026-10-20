package com.example.iiushka.presentation

import com.example.iiushka.AppGraph

fun createChatViewModel(): ChatViewModel = ChatViewModel(
    sendMessageUseCase = AppGraph.sendMessageUseCase,
    clearChatUseCase = AppGraph.clearChatUseCase,
    chatRepository = AppGraph.chatRepository,
)
