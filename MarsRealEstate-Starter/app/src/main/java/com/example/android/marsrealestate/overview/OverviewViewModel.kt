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
 *
 */

package com.example.android.marsrealestate.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.marsrealestate.network.MarsApi
import com.example.android.marsrealestate.network.MarsApiFilter
import com.example.android.marsrealestate.network.MarsProperty
import kotlinx.coroutines.launch


/**
 * The [ViewModel] that is attached to the [OverviewFragment].
 */
class OverviewViewModel : ViewModel() {

    enum class MarsApiStatus { LOADING, ERROR, DONE }

    private val _status = MutableLiveData<MarsApiStatus>()
    val status: LiveData<MarsApiStatus>
        get() = _status

    private val _properties = MutableLiveData<List<MarsProperty>>()
    val properties : LiveData<List<MarsProperty>>
        get() = _properties

    private val _navigateToSelectedPropertyEvent = MutableLiveData<MarsProperty>()
    val navigateToSelectedPropertyEvent : LiveData<MarsProperty>
        get() = _navigateToSelectedPropertyEvent

    init {
        getMarsRealEstateProperties(MarsApiFilter.SHOW_ALL)
    }

    fun updateFilter(filter: MarsApiFilter){
        getMarsRealEstateProperties(filter)
    }

    private fun getMarsRealEstateProperties(filter: MarsApiFilter) {
        _status.value = MarsApiStatus.LOADING
        viewModelScope.launch {
            val properties = try {
                MarsApi.retrofitService.getProperties(filter.value)
            } catch (e: Exception) {
                _status.value = MarsApiStatus.ERROR
                _properties.value = emptyList()
                return@launch
            }
            _status.value = MarsApiStatus.DONE
            _properties.value = properties
        }
    }

    fun onNavigateToSelectedProperty(property: MarsProperty) {
        _navigateToSelectedPropertyEvent.value = property
    }

    fun onNavigateToSelectedPropertyComplete() {
        _navigateToSelectedPropertyEvent.value = null
    }
}
