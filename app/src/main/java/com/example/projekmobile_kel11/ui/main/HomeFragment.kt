package com.example.projekmobile_kel11.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.projekmobile_kel11.databinding.FragmentHomeBinding
import com.example.projekmobile_kel11.ui.prediction.PredictionFormFragment

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnMulaiPrediksi.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(
                    com.example.projekmobile_kel11.R.id.fragment_container,
                    PredictionFormFragment()
                )
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
