package com.example.projekmobile_kel11

import ai.onnxruntime.*
import android.content.Context
import android.util.Log

class OnnxPredictor(context: Context) {

    private val env: OrtEnvironment = OrtEnvironment.getEnvironment()
    private val session: OrtSession

    init {
        val modelBytes = context.assets
            .open("model_RF.onnx")
            .readBytes()

        session = env.createSession(modelBytes)
        Log.d("ONNX", "✅ Model loaded")
    }

    /**
     * input → FloatArray size 19
     * return → Pair(class, probability)
     */
    fun predict(input: FloatArray): Pair<Int, Float> {

        val inputTensor = OnnxTensor.createTensor(
            env,
            java.nio.FloatBuffer.wrap(input),
            longArrayOf(1, input.size.toLong())
        )

        val results = session.run(mapOf("input" to inputTensor))

        var predictedClass = -1
        var confidence = 0f

        // =============================
        // Output selalu OrtValue
        // =============================
        for (entry in results) {

            val ortValue = entry.value
            val value = ortValue.value

            when (value) {

                // LABEL -> LongArray
                is LongArray -> {
                    predictedClass = value[0].toInt()
                }

                // PROBABILITIES -> FloatArray atau Array<FloatArray>
                is Array<*> -> {
                    val arr = value as Array<FloatArray>  // bentuk: [ [p0, p1] ]
                    confidence = arr[0][predictedClass]
                }

                is FloatArray -> {
                    confidence = value[predictedClass]
                }
            }
        }


        inputTensor.close()
        results.close()

        return Pair(predictedClass, confidence)
    }
}
