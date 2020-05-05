package dev.wendyyanto.tictactoe

internal fun Boolean.toInt() = if (this) 1 else 0
internal fun Int.toBoolean() = this == 1