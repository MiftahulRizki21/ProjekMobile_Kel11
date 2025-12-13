import com.example.projekmobile_kel11.data.model.PredictionInput
import com.example.projekmobile_kel11.data.model.PredictionResult
import com.example.projekmobile_kel11.utils.MLHelper

class PredictionRepository(private val mlHelper: MLHelper) {

    fun predictCancerRisk(input: PredictionInput): PredictionResult {

        val (predClass, probability) = mlHelper.predict(input)

        val riskLevel = when (predClass) {
            0 -> "Rendah"
            1 -> "Tinggi"
            else -> "Tidak Diketahui"
        }

        return PredictionResult(
            predictionId = System.currentTimeMillis().toString(),
            tanggalPrediksi = getCurrentDate(),
            riskLevel = riskLevel,
            riskScore = probability,
            input = input
        )
    }

    private fun getCurrentDate(): String {
        val sdf = java.text.SimpleDateFormat(
            "dd MMM yyyy",
            java.util.Locale("id", "ID")
        )
        return sdf.format(java.util.Date())
    }
}
