package com.homanad.android.sample.flowchannelexamples.screens.channel.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChannelViewModel : ViewModel() {

    private val chars = "Hello World! These are Channels example!!!".split("")

    val rendezvousChannel = Channel<String>(Channel.RENDEZVOUS)
    val bufferedChannel = Channel<String>(Channel.BUFFERED)
    val customizedCapacityChannel1 = Channel<String>(5)
    val customizedCapacityChannel2 = Channel<String>(5)

    fun startEmitting() {
        viewModelScope.launch {
            launch {
                chars.forEach {
                    rendezvousChannel.trySend(it)
                    delay(200)
                }
            }
            launch {
                chars.forEach {
                    bufferedChannel.trySend(it)
                    delay(200)
                }
            }
            launch {
                chars.forEach {
                    customizedCapacityChannel1.trySend(it)
                    delay(200)
                }
            }
            launch {
                chars.forEach {
                    customizedCapacityChannel2.send(it)
                    println("--------is sent: $it")
                    delay(200)
                }
            }
        }
    }
}