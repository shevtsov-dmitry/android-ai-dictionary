package app.habit_lens.ui_kotlin

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform