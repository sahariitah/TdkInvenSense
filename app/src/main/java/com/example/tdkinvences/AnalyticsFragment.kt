package com.example.tdkinvences

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tdkinvences.databinding.FragmentAnalyticsBinding
import com.robinhood.spark.SparkAdapter


class AnalyticsFragment : Fragment() {

    private var _binding: FragmentAnalyticsBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel
    private val dataList = mutableListOf<Float>()

    private val adapter = object : SparkAdapter() {
        override fun getY(index: Int): Float = dataList[index]
        override fun getCount(): Int = dataList.size
        override fun getItem(index: Int): Any = dataList[index]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        setupSparkView()
        observeDistanceData()

        return binding.root
    }

    private fun setupSparkView() {
        binding.sparkView.apply {
            lineColor = ContextCompat.getColor(requireContext(), R.color.YellowLight)
            lineWidth = 8f
            adapter = this@AnalyticsFragment.adapter  // Set adapter once
        }
    }

    private fun observeDistanceData() {
        sharedViewModel.distance.observe(viewLifecycleOwner) { distance ->
            Log.d("AnalyticsFragment", "Received distance: $distance mm")

            updateChart(distance)  //  pass Float value
        }
    }

    private fun updateChart(value: Float) {
        dataList.add(value)

        binding.sparkView.adapter?.notifyDataSetChanged()  // Notify adapter about the new value

        binding.currentValueText.text = "Value: ${value.toInt()} mm"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



