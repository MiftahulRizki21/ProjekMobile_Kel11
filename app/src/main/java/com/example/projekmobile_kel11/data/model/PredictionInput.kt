package com.example.projekmobile_kel11.data.model
data class PredictionInput(
    val age: Int,                          // 1. Age
    val gender: Int,                       // 2. Gender (0=Perempuan, 1=Laki-laki)
    val tobaccoUse: Int,                   // 3. Tobacco Use (0=No, 1=Yes)
    val alcoholConsumption: Int,           // 4. Alcohol Consumption (0=No, 1=Yes)
    val hpvInfection: Int,                 // 5. HPV Infection (0=No, 1=Yes)
    val betelQuidUse: Int,                 // 6. Betel Quid Use (0=No, 1=Yes)
    val chronicSunExposure: Int,           // 7. Chronic Sun Exposure (0=No, 1=Yes)
    val poorOralHygiene: Int,              // 8. Poor Oral Hygiene (0=No, 1=Yes)
    val dietFruitsVegetables: Int,         // 9. Diet (0=Buruk, 1=Baik)
    val familyHistoryCancer: Int,          // 10. Family History (0=No, 1=Yes)
    val compromisedImmuneSystem: Int,      // 11. Compromised Immune (0=No, 1=Yes)
    val oralLesions: Int,                  // 12. Oral Lesions (0=No, 1=Yes)
    val unexplainedBleeding: Int,          // 13. Unexplained Bleeding (0=No, 1=Yes)
    val difficultySwallowing: Int,         // 14. Difficulty Swallowing (0=No, 1=Yes)
    val whiteRedPatches: Int,              // 15. White/Red Patches (0=No, 1=Yes)
    val tumorSize: Float,                  // 16. Tumor Size (cm)
    val cancerStage: Int,                  // 17. Cancer Stage (0-4)
    val treatmentType: Int,                // 18. Treatment Type
    val earlyDiagnosis: Int                // 19. Early Diagnosis (0=No, 1=Yes)
)


