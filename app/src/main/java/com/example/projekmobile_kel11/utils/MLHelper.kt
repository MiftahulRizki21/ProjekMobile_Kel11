package com.example.projekmobile_kel11.utils

import android.content.Context
import com.example.projekmobile_kel11.data.model.PredictionInput
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class MLHelper(private val context: Context) {

    private var interpreter: Interpreter? = null

    init {
        loadModel()
    }

    private fun loadModel() {
        try {
            val modelFile = loadModelFile("oral_cancer_model.tflite")
            interpreter = Interpreter(modelFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadModelFile(modelName: String): MappedByteBuffer {
        val assetFileDescriptor = context.assets.openFd(modelName)
        val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun predict(input: PredictionInput): Float {
        // Konversi input ke array float sesuai urutan variabel
        val inputArray = floatArrayOf(
            input.age.toFloat(),
            input.gender.toFloat(),
            input.tobaccoUse.toFloat(),
            input.alcoholConsumption.toFloat(),
            input.hpvInfection.toFloat(),
            input.betelQuidUse.toFloat(),
            input.chronicSunExposure.toFloat(),
            input.poorOralHygiene.toFloat(),
            input.dietFruitsVegetables.toFloat(),
            input.familyHistoryCancer.toFloat(),
            input.compromisedImmuneSystem.toFloat(),
            input.oralLesions.toFloat(),
            input.unexplainedBleeding.toFloat(),
            input.difficultySwallowing.toFloat(),
            input.whiteRedPatches.toFloat(),
            input.tumorSize,
            input.cancerStage.toFloat(),
            input.treatmentType.toFloat(),
            input.earlyDiagnosis.toFloat()
        )

        val outputArray = Array(1) { FloatArray(1) }

        interpreter?.run(arrayOf(inputArray), outputArray)

        return outputArray[0][0]
    }

    fun close() {
        interpreter?.close()
    }
}
