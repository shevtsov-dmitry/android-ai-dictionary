package app.habit_lens.ui_kotlin

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "ui_kotlin",
    ) {
        App()
    }
}