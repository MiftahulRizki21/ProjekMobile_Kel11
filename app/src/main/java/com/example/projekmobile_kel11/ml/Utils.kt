package com.example.projekmobile_kel11.ml

import android.content.Context
import com.google.gson.Gson
import java.io.InputStreamReader

inline fun <reified T> loadJson(context: Context, fileName: String): T {
    val inputStream = context.assets.open(fileName)
    val reader = InputStreamReader(inputStream)
    return Gson().fromJson(reader, T::class.java)
}
