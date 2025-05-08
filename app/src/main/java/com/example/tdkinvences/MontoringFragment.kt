package com.example.tdkinvences


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.tdkinvences.databinding.FragmentMonitoringBinding


class MonitoringFragment : Fragment() {

    private var _binding: FragmentMonitoringBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel

    @OptIn(UnstableApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonitoringBinding.inflate(inflater, container, false)

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        // Observe distance as Float
        sharedViewModel.distance.observe(viewLifecycleOwner) { distance ->
            Log.d("MonitoringFragment", "Received distance: $distance mm")

            val range = distance.toInt() // Convert Float to Int for UI
            Log.d("MonitoringFragment", "Processing range: $range")

            updateUIForRange(range)  // Update UI based on range
        }

        return binding.root
    }

    @OptIn(UnstableApi::class)
    private fun updateUIForRange(range: Int) {
        Log.d("MonitoringFragment", "Updating UI for range: $range")

        when (range) {
            in 1..150 -> { // Critical range
                binding.resultTextCritical.visibility = View.VISIBLE
                binding.resultTextGood.visibility = View.GONE
                binding.resultTextWarning.visibility = View.GONE
                binding.greenLedOn.visibility = View.GONE
                binding.yellowLedOn.visibility = View.GONE
                binding.redLedOn.visibility = View.VISIBLE
            }
            in 151..250 -> { // Warning range
                binding.resultTextWarning.visibility = View.VISIBLE
                binding.resultTextGood.visibility = View.GONE
                binding.resultTextCritical.visibility = View.GONE
                binding.greenLedOn.visibility = View.GONE
                binding.yellowLedOn.visibility = View.VISIBLE
                binding.redLedOn.visibility = View.GONE
            }
            else -> { // Good range
                binding.resultTextGood.visibility = View.VISIBLE
                binding.resultTextWarning.visibility = View.GONE
                binding.resultTextCritical.visibility = View.GONE
                binding.greenLedOn.visibility = View.VISIBLE
                binding.yellowLedOn.visibility = View.GONE
                binding.redLedOn.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
