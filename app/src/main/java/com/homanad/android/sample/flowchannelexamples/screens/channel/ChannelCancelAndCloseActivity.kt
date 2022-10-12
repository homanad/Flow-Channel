package com.homanad.android.sample.flowchannelexamples.screens.channel

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.homanad.android.sample.flowchannelexamples.databinding.ActivityChannelCancelCloseBinding
import com.homanad.android.sample.flowchannelexamples.screens.channel.vm.ChannelCancelAndCloseViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class ChannelCancelAndCloseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChannelCancelCloseBinding
    private val viewModel: ChannelCancelAndCloseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChannelCancelCloseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        testClosedChannel()
        testCanceledChannel()
        testClosedChannelWithCause()
        testCanceledChannelWithCause()
    }

    private fun testClosedChannel() {
        viewModel.startEmittingAndClosing()

        lifecycleScope.launch {
            delay(3000)

            launch {
                if (viewModel.closedChannel.isClosedForSend) {
                    binding.run {
                        text1.text = "${text1.text}\nClosed for send"
                    }
                }

                if (viewModel.closedChannel.isClosedForReceive) {
                    binding.run {
                        text1.text = "${text1.text}$\nClosed for receive"
                    }
                }
            }

            delay(500)

            launch {
                viewModel.closedChannel.receiveAsFlow().collect {
                    binding.run {
                        text1.text = "${text1.text}$it"
                    }
                }
            }
        }
    }

    private fun testCanceledChannel() {
        viewModel.startEmittingAndCancelling()

        lifecycleScope.launch {
            delay(3000)

            launch {
                if (viewModel.canceledChannel.isClosedForSend) {
                    binding.run {
                        text2.text = "${text2.text}\nClosed for send"
                    }
                }

                if (viewModel.canceledChannel.isClosedForReceive) {
                    binding.run {
                        text2.text = "${text2.text}\nClosed for receive"
                    }
                }
            }

            launch {
                viewModel.canceledChannel.receiveAsFlow().collect {
                    binding.run {
                        text2.text = "${text2.text}$it"
                    }
                }
            }
        }
    }

    private fun testClosedChannelWithCause() {
        viewModel.startEmittingAndClosingWithCause()

        lifecycleScope.launch {
            delay(3000)
            launch {
                viewModel.closedWithCauseChannel.invokeOnClose {
                    binding.run {
                        text3.text = "${text3.text}\n${it?.message}"
                    }
                }
            }
            launch {
                Log.i(
                    "testClosedChannelWithCause",
                    "isClosedForSend: ${viewModel.closedWithCauseChannel.isClosedForSend}"
                )
                if (viewModel.closedWithCauseChannel.isClosedForSend) {
                    binding.run {
                        text3.text = "${text3.text}\nClosed for send"
                    }
                }
                Log.i(
                    "testClosedChannelWithCause",
                    "isClosedForReceive: ${viewModel.closedWithCauseChannel.isClosedForReceive}"
                )
                if (viewModel.closedWithCauseChannel.isClosedForReceive) {
                    binding.run {
                        text3.text = "${text3.text}\nClosed for receive"
                    }
                }
            }
            launch {
                try {
                    viewModel.closedWithCauseChannel.receiveAsFlow().collect {
                        binding.run {
                            text3.text = "${text3.text}$it"
                        }
                    }
                } catch (e: Exception) {
                    binding.run {
                        text3.text = "${text3.text}\nException is thrown"
                    }
                }
            }
        }
    }

    private fun testCanceledChannelWithCause() {
        viewModel.startEmittingAndCancellingWithCause()

        lifecycleScope.launch {
            delay(3000)
            launch {
                viewModel.canceledWithCauseChannel.invokeOnClose {
                    binding.run {
                        text4.text = "${text4.text}\n${it?.message}"
                    }
                }
            }
            launch {
                Log.i(
                    "testCanceledChannelWithCause",
                    "isClosedForSend: ${viewModel.canceledWithCauseChannel.isClosedForSend}"
                )
                if (viewModel.canceledWithCauseChannel.isClosedForSend) {
                    binding.run {
                        text4.text = "${text4.text}\nClosed for send"
                    }
                }
                Log.i(
                    "testCanceledChannelWithCause",
                    "isClosedForReceive: ${viewModel.canceledWithCauseChannel.isClosedForReceive}"
                )
                if (viewModel.canceledWithCauseChannel.isClosedForReceive) {
                    binding.run {
                        text4.text = "${text4.text}\nClosed for receive"
                    }
                }
            }
            launch {
                try {
                    viewModel.canceledWithCauseChannel.receiveAsFlow().collect {
                        binding.run {
                            text4.text = "${text4.text}$it"
                        }
                    }
                } catch (e: Exception) {
                    binding.run {
                        text4.text = "${text4.text}\nException is thrown"
                    }
                }
            }
        }
    }
}