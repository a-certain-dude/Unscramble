package com.example.unscramble.ui


/*State ui for the game*/
data class GameUiState(
    val currentScrambledWord: String = "",
    val isUserGuessedWordWrong: Boolean = false,
    val score: Int = 0,
    val wordCount: Int = 1,
    val isGameOver:Boolean = false,
)
