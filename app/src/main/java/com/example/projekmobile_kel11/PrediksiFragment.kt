package com.example.projekmobile_kel11

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment

class PrediksiFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_prediksi, container, false)

        val btnSubmit: Button = view.findViewById(R.id.btnSubmitPrediksi)
        btnSubmit.setOnClickListener {
            // NANTI: proses jawaban & tampilkan popup hasil prediksi
            Toast.makeText(requireContext(), "Submit prediksi (dummy)", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
