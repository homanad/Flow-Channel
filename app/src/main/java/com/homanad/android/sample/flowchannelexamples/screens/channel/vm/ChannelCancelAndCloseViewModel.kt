package com.homanad.android.sample.flowchannelexamples.screens.channel.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException

class ChannelCancelAndCloseViewModel : ViewModel() {

    private val chars = "Hello World! These are Channels example!!!".split("")

    val closedChannel = Channel<String>(Channel.BUFFERED)

    fun startEmittingAndClosing() {
        viewModelScope.launch {
            loop@ for (i in chars.indices) {
                if (i < 5) {
                    closedChannel.send(chars[i])
                    delay(500)
                } else {
                    closedChannel.close()
                    val result = closedChannel.trySend(chars[i])
                    Log.i("startEmittingAndClosing", "isSuccess: ${result.isSuccess}")
                    Log.i("startEmittingAndClosing", "isClosed: ${result.isClosed}")
                    Log.i("startEmittingAndClosing", "isFailure: ${result.isFailure}")
                    break@loop
                }
            }
        }
    }

    val canceledChannel = Channel<String>(Channel.BUFFERED)

    fun startEmittingAndCancelling() {
        viewModelScope.launch {
            loop@ for (i in chars.indices) {
                if (i < 5) {
                    canceledChannel.send(chars[i])
                    delay(500)
                } else {
                    canceledChannel.cancel()
                    val result = canceledChannel.trySend(chars[i])
                    Log.i("startEmittingAndCancelling", "isSuccess: ${result.isSuccess}")
                    Log.i("startEmittingAndCancelling", "isClosed: ${result.isClosed}")
                    Log.i("startEmittingAndCancelling", "isFailure: ${result.isFailure}")
                    break@loop
                }
            }
        }
    }

    val closedWithCauseChannel = Channel<String>(Channel.BUFFERED)

    fun startEmittingAndClosingWithCause() {
        viewModelScope.launch {
            loop@ for (i in chars.indices) {
                if (i < 5) {
                    closedWithCauseChannel.send(chars[i])
                    delay(500)
                } else {
                    closedWithCauseChannel.close(Exception("Close it!"))
                    val result = closedWithCauseChannel.trySend(chars[i])
                    Log.i("startEmittingAndClosingWithCause", "isSuccess: ${result.isSuccess}")
                    Log.i("startEmittingAndClosingWithCause", "isClosed: ${result.isClosed}")
                    Log.i("startEmittingAndClosingWithCause", "isFailure: ${result.isFailure}")
                    break@loop
                }
            }
        }
    }

    val canceledWithCauseChannel = Channel<String>(Channel.BUFFERED)

    fun startEmittingAndCancellingWithCause() {
        viewModelScope.launch {
            loop@ for (i in chars.indices) {
                if (i < 5) {
                    canceledWithCauseChannel.send(chars[i])
                    delay(500)
                } else {
                    canceledWithCauseChannel.cancel(CancellationException("Cancel it!"))
                    val result = canceledWithCauseChannel.trySend(chars[i])
                    Log.i("startEmittingAndCancellingWithCause", "isSuccess: ${result.isSuccess}")
                    Log.i("startEmittingAndCancellingWithCause", "isClosed: ${result.isClosed}")
                    Log.i("startEmittingAndCancellingWithCause", "isFailure: ${result.isFailure}")
                    break@loop
                }
            }
        }
    }
}