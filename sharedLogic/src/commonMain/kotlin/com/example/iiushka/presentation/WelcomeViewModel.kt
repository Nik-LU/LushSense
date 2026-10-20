package com.example.iiushka.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class WelcomeViewModel : ViewModel() {

    private val _navigationEvents = Channel<WelcomeNavigationEvent>(Channel.BUFFERED)
    val navigationEvents: Flow<WelcomeNavigationEvent> = _navigationEvents.receiveAsFlow()

    fun onStartClicked() {
        viewModelScope.launch {
            _navigationEvents.send(WelcomeNavigationEvent.NavigateToChat)
        }
    }

    fun cleanup() {
        viewModelScope.cancel()
    }

}
