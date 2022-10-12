package com.homanad.android.sample.flowchannelexamples.screens.stateFlow.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StateFlowViewModel : ViewModel() {

    private val mStateFlow = MutableStateFlow("")
    val stateFlow: StateFlow<String> = mStateFlow

    fun startEmitting() {
        viewModelScope.launch {
            val chars = "Hello World! This is a StateFlow example!!!".split("")
            chars.forEach {
                mStateFlow.value = it
                delay(200)
            }
        }
    }
}