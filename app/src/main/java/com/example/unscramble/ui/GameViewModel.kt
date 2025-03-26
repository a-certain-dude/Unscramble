package com.example.unscramble.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


/*view model class to survive configuration changes*/
class GameViewModel : ViewModel() {

    /*backing properties*/
    /* holds the internal state of the game, must be updated internally*/
    private val _internalState = MutableStateFlow(GameUiState())

    /*holds the exposed state of the game*/
    val exposedObservableState: StateFlow<GameUiState> = _internalState.asStateFlow()

    var userGuess by mutableStateOf(value = "")
        private set

    private lateinit var currentWord: String

    fun skipWord() {
        updatedGameScreen(updatedScore = _internalState.value.score)
        updateUserGuess(guessedWord = "")
    }


    /*function that verify if the guessed word is the same as the word in the set*/
    fun checkUserGuess() {
        if (userGuess.equals(currentWord, ignoreCase = true)) {
            val updatedScoreIfTrue = _internalState.value.score.plus(SCORE_INCREASE)

            updatedGameScreen(updatedScore = updatedScoreIfTrue)

        } else {
            /*updating the internal state so the the exposedVariable would expose it state to the game screen which is the UI*/
            _internalState.update { currentWordState -> currentWordState.copy(isUserGuessedWordWrong = true) }

            //updatedGameScreen(updatedScore = 0) does not increase the word count when guessed-word is wrong
        }

        updateUserGuess(guessedWord = "")
    }


    private fun updatedGameScreen(updatedScore: Int) {

        if (usedWords.size == MAX_NO_OF_WORDS) {
            /*updating states , we not picking a new word again*/
            _internalState.update { it.copy(score = updatedScore, isUserGuessedWordWrong = false, isGameOver = true) }
        } else {
            /*it.copy would be fine too*/
            _internalState.update { updates ->
                updates.copy(
                    score = updatedScore,
                    isUserGuessedWordWrong = false,
                    currentScrambledWord = pickRandomWordsAndShuffle(),
                    wordCount = updates.wordCount.inc()

                )
            }


        }


    }


    // helper method to pick random word from the list and shuffle it.
    private fun pickRandomWordsAndShuffle(): String {
        currentWord = allWords.random()
        if (usedWords.contains(currentWord)) {
            return pickRandomWordsAndShuffle()
        } else {
            usedWords.add(currentWord)
            return shuffleCurrentWord(currentWord)
        }
    }


    /*set collections to store used words*/
    private var usedWords: MutableSet<String> = mutableSetOf()


    /*method to shuffle the currentWord*/
    private fun shuffleCurrentWord(word: String): String {

        val tempWord = word.toCharArray()
        tempWord.shuffle()

        while (String(tempWord) == word) {
            tempWord.shuffle()
        }

        return String(tempWord)

    }


    fun updateUserGuess(guessedWord: String) {
        userGuess = guessedWord
    }


     fun resetGame() {
        usedWords.clear()
        _internalState.value = GameUiState(currentScrambledWord = pickRandomWordsAndShuffle())

    }


    init {
        resetGame()
    }


}

