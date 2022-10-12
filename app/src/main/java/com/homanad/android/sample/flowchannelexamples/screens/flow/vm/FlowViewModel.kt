package com.homanad.android.sample.flowchannelexamples.screens.flow.vm

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

class FlowViewModel : ViewModel() {

    val flow = flow {
        val chars = "Hello World! This is a Flow example!!!".split("")
        chars.forEach {
            emit(it)
            delay(200)
        }
    }
}