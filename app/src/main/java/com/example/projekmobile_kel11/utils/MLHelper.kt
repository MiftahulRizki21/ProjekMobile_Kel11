package com.example.projekmobile_kel11.utils

import android.content.Context
import com.example.projekmobile_kel11.OnnxPredictor
import com.example.projekmobile_kel11.Preprocessor
import com.example.projekmobile_kel11.data.model.PredictionInput

class MLHelper(context: Context) {

    private val preprocessor = Preprocessor(context)
    private val predictor = OnnxPredictor(context)

    /**
     * Tetap pakai ONNX & Preprocessor
     * Return → Pair(class, probability)
     */
    fun predict(input: PredictionInput): Pair<Int, Float> {
        val inputMap = input.toMap()
        val processed = preprocessor.preprocess(inputMap)
        return predictor.predict(processed)
    }
}
