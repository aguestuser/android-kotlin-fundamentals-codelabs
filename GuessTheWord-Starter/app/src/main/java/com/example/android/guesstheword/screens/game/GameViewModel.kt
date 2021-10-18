package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.ViewModel

class GameViewModel: ViewModel() {
    private val _word = MutableLiveData<String>()
    val word: LiveData<String>
      get() = _word

    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int>
      get() = _score

    private val _eventGameEnded = MutableLiveData<Boolean>()
    val eventGameEnded: LiveData<Boolean>
        get() = _eventGameEnded

    private val _currentTime = MutableLiveData<Long>()
    val currentTime: LiveData<Long>
        get() = _currentTime
    val currentTimeString = map(currentTime) {
        DateUtils.formatElapsedTime(it)
    }

    private val timer: CountDownTimer by lazy {
        object: CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {
            override fun onTick(millisUntilFinished: Long) {
                _currentTime.value = millisUntilFinished / ONE_SECOND
            }
            override fun onFinish() {
                _currentTime.value = DONE
                onEndGame()
            }
        }
    }

    private lateinit var wordList: MutableList<String>

    companion object {
        private const val TAG = "GameViewModel"
        private const val DONE = 0L
        private const val ONE_SECOND = 1000L
        private const val COUNTDOWN_TIME = ONE_SECOND * 20
    }

    init {
        timer.start()
        _word.value = ""
        _score.value = 0
        resetList()
        nextWord()
        Log.i(TAG, "$TAG created")
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
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
        if(wordList.isEmpty()) resetList()
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