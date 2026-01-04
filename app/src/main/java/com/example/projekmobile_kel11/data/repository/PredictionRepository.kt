package com.example.projekmobile_kel11.data.repository

import com.example.projekmobile_kel11.data.model.PredictionResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PredictionRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getUserPredictions(
        userId: String,
        onSuccess: (List<PredictionResult>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection("predictions")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                onSuccess(it.toObjects(PredictionResult::class.java))
            }
            .addOnFailureListener {
                onError(it)
            }
    }

    fun savePrediction(result: PredictionResult) {
        db.collection("predictions").add(result)
    }
}
