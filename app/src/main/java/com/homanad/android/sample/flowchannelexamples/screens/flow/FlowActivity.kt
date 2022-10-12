package com.homanad.android.sample.flowchannelexamples.screens.flow

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.homanad.android.sample.flowchannelexamples.databinding.ActivityFlowBinding
import com.homanad.android.sample.flowchannelexamples.screens.flow.vm.FlowViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FlowActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFlowBinding
    private val viewModel: FlowViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.flow.collect { //this is observer 1
                        binding.run {
                            textObserver1.text = "${textObserver1.text}$it"
                        }
                    }
                }
                delay(1000)
                launch {
                    viewModel.flow.collect { //this is observer 2
                        binding.run {
                            textObserver2.text = "${textObserver2.text}$it"
                        }
                    }
                }
            }
        }

        binding.buttonStartObserver3.setOnClickListener {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.CREATED) {
                    viewModel.flow.collect { //this is observer 3
                        binding.run {
                            textObserver3.text = "${textObserver3.text}$it"
                        }
                    }
                }
            }
        }
    }
}