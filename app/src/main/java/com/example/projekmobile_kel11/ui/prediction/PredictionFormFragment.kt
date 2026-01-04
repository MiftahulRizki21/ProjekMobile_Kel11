package com.example.projekmobile_kel11.ui.prediction

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.data.model.PredictionInput
import com.example.projekmobile_kel11.data.model.PredictionResult
import com.example.projekmobile_kel11.data.repository.PredictionRepository
import com.example.projekmobile_kel11.utils.MLHelper
import com.google.firebase.auth.FirebaseAuth

class PredictionFormFragment : Fragment() {

    private lateinit var mlHelper: MLHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.activity_prediction_form, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mlHelper = MLHelper(requireContext())
        view.findViewById<Button>(R.id.btnSubmit).setOnClickListener {
            runPrediction(view)
        }
    }

    private fun runPrediction(view: View) {
        val age = view.findViewById<EditText>(R.id.etAge).text.toString().toInt()
        val tumorSize = view.findViewById<EditText>(R.id.etTumorSize).text.toString().toDouble()

        val input = PredictionInput(
            age = age,
            gender = "Laki-laki",
            tobacco = "No",
            alcohol = "No",
            hpv = "No",
            betel = "No",
            sun = "No",
            oralHygiene = "No",
            diet = "No",
            familyHistory = "No",
            immune = "No",
            lesions = "No",
            bleeding = "No",
            swallowing = "No",
            patches = "No",
            tumorSize = tumorSize,
            cancerStage = "Unknown",
            treatmentType = "None",
            earlyDiagnosis = "Tidak"
        )

        val (predClass, prob) = mlHelper.predict(input)

        val result = PredictionResult(
            userId = FirebaseAuth.getInstance().currentUser!!.uid,
            timestamp = System.currentTimeMillis(),
            riskLevel = if (predClass == 1) "Tinggi" else "Rendah",
            riskScore = prob.toDouble()
        )

        PredictionRepository().savePrediction(result)
        showResultPopup(result.riskLevel, prob)
    }

    private fun showResultPopup(level: String, prob: Float) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hasil Prediksi")
            .setMessage(
                "Risiko: $level\nAkurasi: ${(prob * 100).toInt()} %"
            )
            .setPositiveButton("OK", null)
            .show()
    }
}
