package com.example.projekmobile_kel11

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.pow

class Preprocessor(private val context: Context) {

    private val TAG = "PREPROCESSOR"

    // JSON CONFIG
    private val mapping: MappingConfig
    private val modelFeatures: List<String>
    private val continuousCols: List<String>
    private val lambdas: List<Double>
    private val mean: List<Double>
    private val scale: List<Double>

    init {
        mapping = loadJson("mapping.json", MappingConfig::class.java)
        modelFeatures = loadJson(
            "model_features.json",
            object : TypeToken<List<String>>() {}.type
        )

        val ptJson = loadJson("power_transformer.json", PTJson::class.java)
        continuousCols = ptJson.continuous_cols
        lambdas = ptJson.lambdas

        val scalerJson = loadJson("scaler.json", ScalerJson::class.java)
        mean = scalerJson.mean
        scale = scalerJson.scale

        Log.d(TAG, "✅ Preprocessor loaded")
    }

    /**
     * MAIN ENTRY
     * Output → FloatArray [1 x 19]
     */
    fun preprocess(input: Map<String, Any>): FloatArray {

        val values = DoubleArray(modelFeatures.size)

        for ((i, feature) in modelFeatures.withIndex()) {

            var x = parseRawValue(feature, input[feature])

            // Power Transformer (ONLY Age & Tumor Size)
            if (continuousCols.contains(feature)) {
                val idx = continuousCols.indexOf(feature)
                x = powerTransform(x, lambdas[idx])

                // Standard Scaler (ONLY same features)
                x = (x - mean[idx]) / scale[idx]
            }

            values[i] = x
        }

        return values.map { it.toFloat() }.toFloatArray()
    }

    // ===============================
    // RAW VALUE PARSER
    // ===============================
    private fun parseRawValue(feature: String, raw: Any?): Double {
        val v = raw?.toString()?.trim()?.lowercase()

        return when (feature) {

            in mapping.yes_no_columns ->
                lookup(mapping.yes_no_map, v)

            "Gender" ->
                lookup(mapping.gender_map, v)

            "Diet (Fruits & Vegetables Intake)" ->
                lookup(mapping.diet_map, v)

            "Treatment Type" ->
                lookup(mapping.treatment_map, v)

            else ->
                raw?.toString()?.toDoubleOrNull() ?: 0.0
        }
    }

    private fun lookup(map: Map<String, Double>, key: String?): Double {
        if (key == null) return 0.0
        return map.entries.firstOrNull {
            it.key.trim().lowercase() == key
        }?.value ?: 0.0
    }

    // ===============================
    // YEO-JOHNSON (TANPA SCALING)
    // ===============================
    private fun powerTransform(x: Double, lambda: Double): Double {
        if (x == 0.0) return 0.0

        val sign = if (x > 0) 1.0 else -1.0
        val y = abs(x)

        return if (lambda != 0.0) {
            sign * ((y.pow(lambda) - 1) / lambda)
        } else {
            sign * ln(y + 1)
        }
    }

    // ===============================
    // JSON LOADER
    // ===============================
    private fun <T> loadJson(file: String, clazz: Class<T>): T {
        val json = context.assets.open(file).bufferedReader().use { it.readText() }
        return Gson().fromJson(json, clazz)
    }

    private fun <T> loadJson(file: String, type: java.lang.reflect.Type): T {
        val json = context.assets.open(file).bufferedReader().use { it.readText() }
        return Gson().fromJson(json, type)
    }

    // ===============================
    // DATA MODELS
    // ===============================
    data class MappingConfig(
        val yes_no_columns: List<String>,
        val yes_no_map: Map<String, Double>,
        val gender_map: Map<String, Double>,
        val diet_map: Map<String, Double>,
        val treatment_map: Map<String, Double>
    )

    data class PTJson(
        val continuous_cols: List<String>,
        val lambdas: List<Double>
    )

    data class ScalerJson(
        val mean: List<Double>,
        val scale: List<Double>
    )
}
