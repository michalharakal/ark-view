package sk.ai.net.client.arc.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sk.ai.net.client.arc.model.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(chatViewModel: ChatViewModel) {
    val uiState by chatViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Agent Url:",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = uiState.agentUrl,
                            onValueChange = chatViewModel::onAgentUrlChanged,
                            singleLine = true,
                            label = { Text("Enter URL") },
                            modifier = Modifier.widthIn(min = 150.dp, max = 250.dp)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Conversation
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AgentHeader(name = "weather-agent")
                uiState.messages.forEach { msg ->
                    MessageBubble(
                        text = msg.text,
                        isUserMessage = msg.isUserMessage
                    )
                }
            }

            // Bottom text-field & send button
            OutgoingMessageRow(
                textValue = uiState.inputText,
                onTextChanged = chatViewModel::onInputChanged,
                onSendClick = chatViewModel::sendMessage
            )
        }
    }
}

@Composable
fun AgentHeader(name: String) {
    Text(
        text = name,
        style = MaterialTheme.typography.titleMedium.copy(
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
    )
}

/**
 * A single message bubble with a small "Copy" icon in the top-right corner.
 */
@Composable
fun MessageBubble(
    text: String,
    isUserMessage: Boolean
) {
    // Clipboard manager (on Android, etc.)
    val clipboardManager = LocalClipboardManager.current

    // Bubble styling
    val bubbleColor = if (isUserMessage) Color(0xFFFFEBEE) else Color(0xFF7E506B)
    val textColor = if (isUserMessage) Color.Black else Color.White
    val alignment = if (isUserMessage) Arrangement.Start else Arrangement.End

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = alignment
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = bubbleColor,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .widthIn(max = 300.dp)
        ) {
            // We’ll stack the text and icon in a Box
            Box {
                // Give the text some right/bottom padding to avoid overlapping the icon
                Text(
                    text = text,
                    color = textColor,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                    modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
                )

                // Place a smaller icon in the lower right corner
                IconButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(text))
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(24.dp) // Smaller overall IconButton container
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Send,
                        contentDescription = "Copy message",
                        tint = textColor.copy(alpha = 0.8f),
                        modifier = Modifier.size(16.dp) // Smaller icon graphic
                    )
                }
            }
        }
    }
}

@Composable
fun OutgoingMessageRow(
    textValue: String,
    onTextChanged: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = textValue,
            onValueChange = onTextChanged,
            modifier = Modifier.weight(1f),
            label = { Text("Type your message...") }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = onSendClick) {
            Text("Send")
        }
    }
}
