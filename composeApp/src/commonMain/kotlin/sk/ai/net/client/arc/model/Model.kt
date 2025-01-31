package sk.ai.net.client.arc.model

data class Message(val text: String, val isUserMessage: Boolean)

data class ChatUiState(
    val agentUrl: String = "http://localhost:8080",
    val messages: List<Message> = emptyList(),
    val inputText: String = ""
)