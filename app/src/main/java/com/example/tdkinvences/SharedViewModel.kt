package com.example.tdkinvences

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel :ViewModel() {

    private val _distance = MutableLiveData<Float>()
    val distance: LiveData<Float> get() = _distance

    fun setDistance(value: Float) {
        _distance.postValue(value)
    }
}