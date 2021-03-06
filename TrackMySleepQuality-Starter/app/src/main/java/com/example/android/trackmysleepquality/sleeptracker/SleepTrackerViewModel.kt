/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import androidx.lifecycle.*
import androidx.lifecycle.Transformations.map
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.launch

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
        val database: SleepDatabaseDao,
        application: Application
): AndroidViewModel(application) {

        private val tonight = MutableLiveData<SleepNight?>()
        val isStartButtonVisible = map(tonight) {
                it == null
        }
        val isStopButtonVisible = map(tonight) {
                it != null
        }

        private val nights = database.getAllNights()
        val nightsString = map(nights) {
                formatNights(it, application.resources)
        }
        val isClearButtonVisible = map(nights) {
                it?.isNotEmpty() ?: false
        }

        private val _navigateToSleepQuality = MutableLiveData<SleepNight>()
        val navigateToSleepQuality: LiveData<SleepNight>
                get() = _navigateToSleepQuality

        private val _showSnackBar = MutableLiveData<Boolean>()
        val showSnackbar: LiveData<Boolean>
                get() = _showSnackBar

        companion object {
                const val TAG = "SleepTrackerViewModel"
        }

        init {
                viewModelScope.launch {
                        tonight.value = getTonight()
                }
        }

        fun onClear() {
                viewModelScope.launch {
                        clear()
                        tonight.value = null
                }
        }

        fun onStartTracking() {
                viewModelScope.launch {
                        val newNight = SleepNight()
                        insert(newNight)
                        tonight.value = getTonight()
                }
        }

        fun onStopTracking() {
                viewModelScope.launch {
                        val oldNight = tonight.value ?: return@launch
                        oldNight.endTimeMilli = System.currentTimeMillis()
                        update(oldNight)
                        _navigateToSleepQuality.value = oldNight
                }
        }

        // helpers

        private suspend fun clear() {
                database.clear()
                _showSnackBar.value = true
        }

        private suspend fun getTonight(): SleepNight? {
                val night = database.getTonight()
                val isComplete = night?.endTimeMilli != night?.startTimeMilli
                return if (isComplete) null else night
        }

        private suspend fun insert(night: SleepNight) {
                database.insert(night)
        }

        private suspend fun update(night: SleepNight) {
                database.update(night)
        }

        fun onNavigationComplete() {
                _navigateToSleepQuality.value = null
        }

        fun onShowSnackBarComplete(){
                _showSnackBar.value = false
        }
}