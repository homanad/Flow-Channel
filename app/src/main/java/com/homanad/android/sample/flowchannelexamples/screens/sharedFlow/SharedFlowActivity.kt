package com.homanad.android.sample.flowchannelexamples.screens.sharedFlow

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.homanad.android.sample.flowchannelexamples.databinding.ActivityFlowBinding
import com.homanad.android.sample.flowchannelexamples.screens.sharedFlow.vm.SharedFlowViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SharedFlowActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFlowBinding
    private val viewModel: SharedFlowViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.startEmitting()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                delay(1300)
                launch {
                    viewModel.sharedFlow.collect { //Observer 1
                        binding.run {
                            textObserver1.text = "${textObserver1.text}$it"
                        }
                    }
                }
                delay(1700)
                launch {
                    viewModel.sharedFlow.collect { //Observer 2
                        binding.run {
                            textObserver2.text = "${textObserver2.text}$it"
                        }
                    }
                }
            }
        }

        binding.buttonStartObserver3.setOnClickListener {
            lifecycleScope.launch {
                viewModel.sharedFlow.collect { //Observer 3
                    binding.run {
                        textObserver3.text = "${textObserver3.text}$it"
                    }
                }
            }
        }
    }
}