package sk.ai.net.client.arc.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.Preview
import sk.ai.net.client.arc.model.ChatViewModel

@Preview
@Composable
fun App() {
    MaterialTheme(
        colorScheme = lightColorScheme(), // or darkColorScheme()
        typography = Typography()
    ) {
        MainScreen(ChatViewModel())
    }
}