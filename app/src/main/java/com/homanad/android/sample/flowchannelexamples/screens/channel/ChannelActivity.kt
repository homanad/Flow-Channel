package com.homanad.android.sample.flowchannelexamples.screens.channel

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.homanad.android.sample.flowchannelexamples.databinding.ActivityChannelBinding
import com.homanad.android.sample.flowchannelexamples.screens.channel.vm.ChannelViewModel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ChannelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChannelBinding
    private val viewModel: ChannelViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChannelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.startEmitting()

        with(binding) {
            button1.setOnClickListener {
                lifecycleScope.launch {
                    viewModel.rendezvousChannel.receiveAsFlow().collect {
                        binding.run {
                            text1.text = "${text1.text}$it"
                        }
                    }
                }
            }

            button2.setOnClickListener {
                lifecycleScope.launch {
                    viewModel.bufferedChannel.receiveAsFlow().collect {
                        binding.run {
                            text2.text = "${text2.text}$it"
                        }
                    }
                }
            }

            button3.setOnClickListener {
                lifecycleScope.launch {
                    viewModel.customizedCapacityChannel1.receiveAsFlow().collect {
                        binding.run {
                            text3.text = "${text3.text}$it"
                        }
                    }
                }
            }

            button4.setOnClickListener {
                lifecycleScope.launch {
                    viewModel.customizedCapacityChannel2.receiveAsFlow().collect {
                        binding.run {
                            text4.text = "${text4.text}$it"
                        }
                    }
                }
            }
        }
    }
}