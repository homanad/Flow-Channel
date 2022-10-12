package com.homanad.android.sample.flowchannelexamples

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.homanad.android.sample.flowchannelexamples.screens.channel.ChannelCancelAndCloseActivity
import com.homanad.android.sample.flowchannelexamples.databinding.ActivityMainBinding
import com.homanad.android.sample.flowchannelexamples.screens.channel.ChannelActivity
import com.homanad.android.sample.flowchannelexamples.screens.flow.FlowActivity
import com.homanad.android.sample.flowchannelexamples.screens.sharedFlow.SharedFlowActivity
import com.homanad.android.sample.flowchannelexamples.screens.stateFlow.StateFlowActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.run {
            buttonFlow.setOnClickListener {
                startActivity(Intent(this@MainActivity, FlowActivity::class.java))
            }
            buttonStateFlow.setOnClickListener {
                startActivity(Intent(this@MainActivity, StateFlowActivity::class.java))
            }
            buttonSharedFlow.setOnClickListener {
                startActivity(Intent(this@MainActivity, SharedFlowActivity::class.java))
            }
            buttonChannel.setOnClickListener {
                startActivity(Intent(this@MainActivity, ChannelActivity::class.java))
            }
            buttonChannel2.setOnClickListener {
                startActivity(
                    Intent(this@MainActivity, ChannelCancelAndCloseActivity::class.java)
                )
            }
        }
    }
}