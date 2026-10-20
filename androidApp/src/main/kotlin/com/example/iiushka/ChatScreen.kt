package com.example.iiushka

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.iiushka.domain.ChatMessage
import com.example.iiushka.presentation.ChatEvent
import com.example.iiushka.presentation.ChatUiState
import com.example.iiushka.presentation.ChatViewModel
import com.example.iiushka.presentation.createChatViewModel
import com.example.iiushka.ui.theme.AppColors
import kotlin.math.roundToInt

@Composable
fun ChatScreen(
    onNavigateBack: () -> Unit,
    vm: ChatViewModel = viewModel { createChatViewModel() },
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        vm.events.collect { event ->
            when (event) {
                is ChatEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppColors.Background,
    ) {
        Scaffold(
            containerColor = AppColors.Background,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopBar(
                    enabled = state is ChatUiState.Content,
                    onClear = { vm.clearChat() },
                    onNavigateBack = onNavigateBack,
                )
            },
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .imePadding(),
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    when (val s = state) {
                        is ChatUiState.Loading -> LoadingIndicator()
                        is ChatUiState.Error -> ErrorState(s.message)
                        is ChatUiState.Content -> {
                            if (s.messages.isEmpty()) {
                                EmptyState()
                            } else {
                                MessagesList(s.messages)
                            }
                            if (s.isSending) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(bottom = 20.dp),
                                    contentAlignment = Alignment.BottomCenter,
                                ) {
                                    LoadingBubble()
                                }
                            }
                        }
                    }
                }

                val sending = (state as? ChatUiState.Content)?.isSending == true
                InputBar(
                    enabled = !sending,
                    onSend = { text -> vm.sendMessage(text) },
                )
            }
        }
    }
}

// ─── TopBar ────────────────────────────────────────────

@Composable
private fun TopBar(
    enabled: Boolean,
    onClear: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppColors.Surface)
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.content_description_back),
                    tint = AppColors.TextPrimary,
                )
            }

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape),
            )
            Spacer(Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.chat_title),
                    color = AppColors.TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    stringResource(R.string.chat_subtitle),
                    color = AppColors.TextSecondary,
                    fontSize = 12.sp,
                )
            }

            IconButton(onClick = onClear, enabled = enabled) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.content_description_clear),
                    tint = AppColors.TextSecondary,
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(AppColors.Divider),
        )
    }
}

// ─── Лента сообщений ───────────────────────────────────

@Composable
private fun MessagesList(messages: List<ChatMessage>) {
    val listState = rememberLazyListState()
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(messages, key = { it.id }) { msg -> MessageBubble(msg) }
    }
}

@Composable
private fun MessageBubble(msg: ChatMessage) {
    val bubbleColor = if (msg.isFromUser) AppColors.UserBubble else AppColors.AssistantBubble
    val alignment = if (msg.isFromUser) Alignment.CenterEnd else Alignment.CenterStart
    val shape = if (msg.isFromUser) {
        RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp)
    } else {
        RoundedCornerShape(18.dp, 18.dp, 18.dp, 4.dp)
    }

    val textColor = when {
        msg.isFromUser -> AppColors.TextPrimary
        msg.positive == true -> AppColors.Positive
        msg.positive == false -> AppColors.Negative
        else -> AppColors.TextPrimary
    }

    val displayText = if (!msg.isFromUser && msg.positive != null) {
        val emoji = if (msg.positive == true) "😊" else "😔"
        "$emoji ${msg.text}"
    } else {
        msg.text
    }

    val meta = msg.confidence?.let { stringResource(R.string.chat_confidence, (it * 100).roundToInt()) }

    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment,
    ) {
        val maxBubbleWidth = maxWidth * 0.82f
        Column(
            modifier = Modifier
                .widthIn(max = maxBubbleWidth)
                .clip(shape)
                .background(bubbleColor)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = if (msg.isFromUser) Alignment.End else Alignment.Start,
        ) {
            Text(
                text = displayText,
                color = textColor,
                fontSize = 15.sp,
                fontWeight = if (!msg.isFromUser && msg.positive != null) {
                    FontWeight.SemiBold
                } else {
                    FontWeight.Normal
                },
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )
            if (meta != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = meta,
                    color = AppColors.TextSecondary,
                    fontSize = 12.sp,
                )
            }
        }
    }
}

// ─── Empty / Loading / Error ───────────────────────────

@Composable
private fun EmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(AppColors.Surface),
                contentAlignment = Alignment.Center,
            ) {
                Text("💬", fontSize = 32.sp)
            }
            Spacer(Modifier.height(16.dp))
            Text(
                stringResource(R.string.chat_empty_state),
                color = AppColors.TextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 40.dp),
            )
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = AppColors.TextSecondary)
    }
}

@Composable
private fun ErrorState(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = message,
            color = AppColors.Negative,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp),
        )
    }
}

@Composable
private fun LoadingBubble() {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(AppColors.AssistantBubble)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(14.dp),
            strokeWidth = 2.dp,
            color = AppColors.TextSecondary,
        )
        Spacer(Modifier.width(10.dp))
        Text(stringResource(R.string.chat_loading), color = AppColors.TextSecondary, fontSize = 14.sp)
    }
}

// ─── Input ─────────────────────────────────────────────

@Composable
private fun InputBar(enabled: Boolean, onSend: (String) -> Unit) {
    var input by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.Background)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        TextField(
            value = input,
            onValueChange = { input = it },
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(22.dp)),
            enabled = enabled,
            placeholder = {
                Text(stringResource(R.string.chat_input_placeholder), color = AppColors.TextSecondary)
            },
            maxLines = 4,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(
                onSend = {
                    if (enabled && input.isNotBlank()) {
                        onSend(input)
                        input = ""
                    }
                },
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = AppColors.InputBackground,
                unfocusedContainerColor = AppColors.InputBackground,
                disabledContainerColor = AppColors.InputBackground,
                focusedTextColor = AppColors.TextPrimary,
                unfocusedTextColor = AppColors.TextPrimary,
                disabledTextColor = AppColors.TextSecondary,
                cursorColor = AppColors.Accent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
        )
        Spacer(Modifier.width(8.dp))
        IconButton(
            onClick = {
                if (enabled && input.isNotBlank()) {
                    onSend(input)
                    input = ""
                }
            },
            enabled = enabled && input.isNotBlank(),
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    if (enabled && input.isNotBlank()) AppColors.Accent
                    else AppColors.InputBackground
                ),
        ) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowUp,
                contentDescription = stringResource(R.string.content_description_send),
                tint = if (enabled && input.isNotBlank()) AppColors.Background
                else AppColors.TextSecondary,
            )
        }
    }
}
