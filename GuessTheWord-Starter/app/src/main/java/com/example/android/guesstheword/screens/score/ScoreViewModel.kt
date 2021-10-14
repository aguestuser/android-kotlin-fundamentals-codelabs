package com.example.android.guesstheword.screens.score

import android.util.Log
import androidx.lifecycle.ViewModel

// TODO: get rid of redundant assignment
class ScoreViewModel(val score: Int): ViewModel() {
    init {
        Log.i("ScoreViewModel", "Final score is: $score")
    }
}