package com.kodeco.android.aam.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.halilibo.richtext.commonmark.Markdown
import com.halilibo.richtext.ui.material3.RichText
import com.kodeco.android.aam.R
import com.kodeco.android.aam.llm.InferenceManager.Companion.MAX_TOKENS
import kotlinx.coroutines.flow.StateFlow

@Composable
internal fun ChatRoute(
  viewModel: ChatViewModel,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val isInputEnabled by viewModel.isInputEnabled.collectAsStateWithLifecycle()

  ChatScreen(
    messages = uiState.messages,
    isInputEnabled = isInputEnabled,
    remainingTokens = viewModel.tokensRemaining,
    onSendMessage = { message ->
      viewModel.sendMessage(message)
    },
    onChangedMessage = { message ->
      viewModel.calculateRemainingTokens(message)
    },
    onClickReset = {
      viewModel.resetChat()
    },
  )
}

@Composable
fun ChatScreen(
  messages: List<ChatMessage>,
  isInputEnabled: Boolean,
  remainingTokens: StateFlow<Int>,
  onClickReset: () -> Unit,
  onSendMessage: (String) -> Unit,
  onChangedMessage: (String) -> Unit,
) {
  var userMessage by rememberSaveable { mutableStateOf("") }
  val tokens by remainingTokens.collectAsState(initial = -1)

  Column(
    modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom
  ) {
    // Top Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      val tokensRemaining = if (tokens >= 0) tokens else MAX_TOKENS
      Text(
        text = "$tokensRemaining ${stringResource(R.string.tokens_remaining)}",
        style = MaterialTheme.typography.headlineSmall
      )

      IconButton(
        onClick = {
          onClickReset()
        },
        enabled = isInputEnabled
      ) {
        Icon(Icons.Default.Refresh, contentDescription = "Reset Chat")
      }
    }

    if (tokens == 0) {
      // Show warning label that context is full
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.LightGray)
          .padding(8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = stringResource(R.string.context_full_message),
          style = MaterialTheme.typography.bodyMedium,
          color = Color.Red,
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth()
        )
      }
    }

    LazyColumn(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .padding(horizontal = 8.dp),
      reverseLayout = true
    ) {
      items(messages) { chat ->
        ChatItem(chat)
      }
    }

    Spacer(modifier = Modifier.width(8.dp))

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 16.dp, horizontal = 4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {

      TextField(
        value = userMessage, onValueChange = {
          userMessage = it
          if (!userMessage.contains(" ") || userMessage.trim() != userMessage) {
            onChangedMessage(userMessage)
          }
        },
        keyboardOptions = KeyboardOptions(
          capitalization = KeyboardCapitalization.Sentences,
        ),
        label = {
          Text(stringResource(R.string.chat_label))
        },
        modifier = Modifier
          .weight(0.85f)
          .padding(horizontal = 16.dp)
          .onFocusChanged { focusState ->
            if (focusState.isFocused) {
              onChangedMessage(userMessage)
            }
          },
        enabled = isInputEnabled
      )

      IconButton(
        onClick = {
          if (userMessage.isNotBlank()) {
            onSendMessage(userMessage)
            userMessage = ""
          }
        },
        modifier = Modifier
          .align(Alignment.CenterVertically)
          .fillMaxWidth()
          .weight(0.10f),
        enabled = isInputEnabled
      ) {
        Icon(
          Icons.AutoMirrored.Default.Send,
          contentDescription = stringResource(R.string.action_send),
          modifier = Modifier
        )
      }
    }
  }
}

@Composable
fun ChatItem(
  chatMessage: ChatMessage
) {
  val bubbleShape = if (chatMessage.isFromUser) {
    RoundedCornerShape(20.dp, 4.dp, 20.dp, 20.dp)
  } else {
    RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
  }

  val horizontalAlignment = if (chatMessage.isFromUser) {
    Alignment.End
  } else {
    Alignment.Start
  }

  Column(
    horizontalAlignment = horizontalAlignment,
    modifier = Modifier
      .padding(horizontal = 8.dp, vertical = 4.dp)
      .fillMaxWidth()
  ) {
    val author = if (chatMessage.isFromUser) {
      stringResource(R.string.user_label)
    } else if (chatMessage.isThinking) {
      stringResource(R.string.thinking_label)
    } else {
      stringResource(R.string.model_label)
    }
    Text(
      text = author,
      style = MaterialTheme.typography.bodyMedium,
      modifier = Modifier.padding(bottom = 4.dp)
    )
    Row {
      BoxWithConstraints {
        Card(
          shape = bubbleShape, modifier = Modifier.widthIn(0.dp, maxWidth * 0.9f)
        ) {
          if (chatMessage.isLoading) {
            CircularProgressIndicator(
              modifier = Modifier.padding(16.dp)
            )
          } else {
            RichText(modifier = Modifier.padding(16.dp)) {
              Markdown(content = chatMessage.message)
            }
          }
        }
      }
    }
  }
}
