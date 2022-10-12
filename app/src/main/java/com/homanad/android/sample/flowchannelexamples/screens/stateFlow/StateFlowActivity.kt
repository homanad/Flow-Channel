package com.homanad.android.sample.flowchannelexamples.screens.stateFlow

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.homanad.android.sample.flowchannelexamples.databinding.ActivityFlowBinding
import com.homanad.android.sample.flowchannelexamples.screens.stateFlow.vm.StateFlowViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StateFlowActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFlowBinding
    private val viewModel: StateFlowViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.startEmitting()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.stateFlow.collect { // observer 1
                        binding.run {
                            textObserver1.text = "${textObserver1.text}$it"
                        }
                    }
                }
                delay(1500)
                launch {
                    viewModel.stateFlow.collect { // observer 2
                        binding.run {
                            textObserver2.text = "${textObserver2.text}$it"
                        }
                    }
                }
            }
        }

        binding.buttonStartObserver3.setOnClickListener {
            lifecycleScope.launch {
                viewModel.stateFlow.collect {
                    viewModel.stateFlow.collect { // observer 3
                        binding.run {
                            textObserver3.text = "${textObserver3.text}$it"
                        }
                    }
                }
            }
        }
    }
}