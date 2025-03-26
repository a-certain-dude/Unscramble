package com.example.unscramble.ui.test

import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.getUnscrambledWord
import com.example.unscramble.ui.GameViewModel
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test

/*Test strategy is made up of
* Success path
* Error path
* Boundary case
* */

/*thingUnderTest = gameViewModel
TriggerOfTest = CorrectWordGuessed
ResultOfTest = ScoreUpdatedAndErrorFlagUnset*/

class GameViewModelTest {

    // creating a variable to instantiate the GameViewModel
    private val viewModel: GameViewModel = GameViewModel()
    /*creating a success path test*/

    @Test
            /**
             * Tests that the game view model correctly updates the game state when the user guesses the correct word.
             */
    fun gameViewModel_CorrectWordGuessed_ScoreUpdatedAndErrorFlagUnset() {
        // Get the current game state from the view model
        var currentGameState = viewModel.exposedObservableState.value

        // Generate the correct word from the scrambled word in the current game state
        val correctPlayedWord = getUnscrambledWord(currentGameState.currentScrambledWord)

        // Update the user's guess in the view model
        viewModel.updateUserGuess(guessedWord = correctPlayedWord)

        // Check the user's guess in the view model
        viewModel.checkUserGuess()

        // Get the updated game state from the view model
        currentGameState = viewModel.exposedObservableState.value

        // Assert that the user's guess is marked as correct and the "wrong guess" flag is reset
        assertFalse(currentGameState.isUserGuessedWordWrong)
        assertEquals(SCORE_AFTER_FIRST_CORRECT_ANSWER, currentGameState.score)
    }


    @Test
    fun gameViewModel_IncorrectGuessedWord_SetErrorFlag() {
        /*without the value appended you can't access the states*/
        /*currentGameState can only access variables whereas viewModel access fields inside GameViewModel*/
        var currentGameState = viewModel.exposedObservableState.value

        val wrongWordPlayed = getUnscrambledWord("goa")
        viewModel.updateUserGuess(wrongWordPlayed)
        viewModel.checkUserGuess()
        currentGameState = viewModel.exposedObservableState.value

        assertTrue(currentGameState.isUserGuessedWordWrong)
        assertEquals(0, currentGameState.score)


    }

    @Test  /*Boundary case test----test whether the states are initiated*/
    // This test verifies that the game initializes correctly when first loaded
    fun gameViewModel_Initialization_FirstWordLoad() {

        // 1. GET CURRENT GAME STATE
        // Gets the current state of the game from the ViewModel
        // This contains all the game data like score, current word, etc.
        val newGameState = viewModel.exposedObservableState.value

        // 2. GET UNSCRAMBLED VERSION OF CURRENT WORD
        // The game shows scrambled words - this gets the original (correct) version
        // For example, if scrambled word is "elhl", correct word might be "hell"
        var correctWord: String = getUnscrambledWord(newGameState.currentScrambledWord)

        // 3. VERIFY THE SCRAMBLED WORD IS ACTUALLY DIFFERENT
        // Checks that the scrambled word isn't the same as the correct word
        // This ensures the scrambling actually worked
        assertNotEquals(correctWord, newGameState.currentScrambledWord)

        // 4. CHECK INITIAL GAME STATE VALUES:

        // a) Word count should start at 1
        // (We're on the first word of the game)
        assertTrue(newGameState.wordCount == 1)

        // b) Score should start at 0
        // (Player hasn't earned any points yet)
        assertTrue(newGameState.score == 0)

        // c) Game shouldn't be over
        // (We just started playing!)
        assertFalse(newGameState.isGameOver)

        // d) No wrong guesses yet
        // (Player hasn't made any guesses at this point)
        assertFalse(newGameState.isUserGuessedWordWrong)
    }


    @Test
    // This test checks if the game updates correctly after guessing all words
    fun gameViewModel_AfterAllGuess_StatesAreUpdatedCorrectly() {

        // Initialize expected score at 0 (starting score)
        var expectedScore = 0

        // Get the current game state from the ViewModel
        var updatedGameStates = viewModel.exposedObservableState.value

        // Get the correct unscrambled version of the current word
        var correctPlayerWord = getUnscrambledWord(updatedGameStates.currentScrambledWord)

        // Repeat for each word in the game (MAX_NO_OF_WORDS times)
        repeat(MAX_NO_OF_WORDS) {
            // Increase the expected score after each correct guess
            expectedScore += SCORE_AFTER_FIRST_CORRECT_ANSWER  // Could also use SCORE_INCREASE

            // Simulate the player guessing the correct word
            viewModel.updateUserGuess(guessedWord = correctPlayerWord)

            // Check if the guess is correct
            viewModel.checkUserGuess()

            // Update the game state after checking the guess
            updatedGameStates = viewModel.exposedObservableState.value

            // Get the correct word for the next round
            correctPlayerWord = getUnscrambledWord(updatedGameStates.currentScrambledWord)

            // Verify that the score matches the expected score
            assertEquals(expectedScore, updatedGameStates.score)
        }

        // After all words are guessed, check:

        // 1. The word count should equal the total number of words (MAX_NO_OF_WORDS)
        assertEquals(MAX_NO_OF_WORDS, updatedGameStates.wordCount)

        // 2. The game should be marked as over (isGameOver = true)
        assertTrue(updatedGameStates.isGameOver)
    }

    @Test
    fun gameViewModel_wordSkip_ScoreUnchangedAndWordCountIncreased() {

        var currentGameStates = viewModel.exposedObservableState.value
        val correctPlayerWord = getUnscrambledWord(currentGameStates.currentScrambledWord)
        viewModel.updateUserGuess(correctPlayerWord)
        viewModel.checkUserGuess()
        currentGameStates = viewModel.exposedObservableState.value
        val lastWordCount = currentGameStates.wordCount
        viewModel.skipWord()
        currentGameStates = viewModel.exposedObservableState.value

        assertEquals(SCORE_INCREASE,currentGameStates.score)
        assertEquals(lastWordCount+1,currentGameStates.wordCount)
    }

    companion object {
        private const val SCORE_AFTER_FIRST_CORRECT_ANSWER = SCORE_INCREASE
    }


}