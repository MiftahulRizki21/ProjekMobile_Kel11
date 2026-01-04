package com.example.projekmobile_kel11.data.model

data class PredictionInput(
    val age: Int = 0,
    val gender: String = "",
    val tobacco: String = "",
    val alcohol: String = "",
    val hpv: String = "",
    val betel: String = "",
    val sun: String = "",
    val oralHygiene: String = "",
    val diet: String = "",
    val familyHistory: String = "",
    val immune: String = "",
    val lesions: String = "",
    val bleeding: String = "",
    val swallowing: String = "",
    val patches: String = "",
    val tumorSize: Double = 0.0,
    val cancerStage: String = "",
    val treatmentType: String = "",
    val earlyDiagnosis: String = ""
) {
    fun toMap(): Map<String, Any> = mapOf(
        "Age" to age,
        "Gender" to gender,
        "Tobacco Use" to tobacco,
        "Alcohol Consumption" to alcohol,
        "HPV Infection" to hpv,
        "Betel Quid Use" to betel,
        "Chronic Sun Exposure" to sun,
        "Poor Oral Hygiene" to oralHygiene,
        "Diet (Fruits & Vegetables Intake)" to diet,
        "Family History of Cancer" to familyHistory,
        "Compromised Immune System" to immune,
        "Oral Lesions" to lesions,
        "Unexplained Bleeding" to bleeding,
        "Difficulty Swallowing" to swallowing,
        "White or Red Patches in Mouth" to patches,
        "Tumor Size (cm)" to tumorSize,
        "Cancer Stage" to cancerStage,
        "Treatment Type" to treatmentType,
        "Early Diagnosis" to earlyDiagnosis
    )
}

