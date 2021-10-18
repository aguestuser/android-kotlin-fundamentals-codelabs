package com.example.android.guesstheword.screens.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel: ViewModel() {
    companion object {
        val TAG = "GameViewModel"
    }

    private val _word = MutableLiveData<String>()
    val word: LiveData<String>
      get() = _word

    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int>
      get() = _score

    private val _eventGameEnded = MutableLiveData<Boolean>()
    val eventGameEnded: LiveData<Boolean>
        get() = _eventGameEnded



    private lateinit var wordList: MutableList<String>

    init {
        _word.value = ""
        _score.value = 0
        resetList()
        nextWord()
        Log.i(TAG, "$TAG created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, "$TAG destroyed")
    }

    /**
     * Resets the list of words and randomizes the order
     */
    private fun resetList() {
        wordList = mutableListOf(
            "queen",
            "hospital",
            "basketball",
            "cat",
            "change",
            "snail",
            "soup",
            "calendar",
            "sad",
            "desk",
            "guitar",
            "home",
            "railway",
            "zebra",
            "jelly",
            "car",
            "crow",
            "trade",
            "bag",
            "roll",
            "bubble"
        )
        wordList.shuffle()
    }

    /**
     * Moves to the next word in the list
     */
    private fun nextWord() {
        if(wordList.isEmpty()) onEndGame()
        else _word.value = wordList.removeAt(0)
    }

    fun onSkip() {
        _score.value = (score.value)?.minus(1)
        nextWord()
    }

    fun onCorrect() {
        _score.value = (score.value)?.plus(1)
        nextWord()
    }

    fun onEndGame() {
        _eventGameEnded.value = true
    }

    fun onEndGameComplete() {
        _eventGameEnded.value = false
    }
}