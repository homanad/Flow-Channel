package com.homanad.android.sample.flowchannelexamples.screens.sharedFlow.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class SharedFlowViewModel : ViewModel() {

    private val _sharedFlow = MutableSharedFlow<String>(3)
    val sharedFlow: SharedFlow<String> = _sharedFlow

    fun startEmitting() {
        val chars = "Hello World! This is a SharedFlow example!!!".split("")
        viewModelScope.launch {
            chars.forEach {
                _sharedFlow.emit(it)
                delay(200)
            }
        }
    }
}