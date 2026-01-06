package com.example.projekmobile_kel11.utils

import android.content.Context
import com.example.projekmobile_kel11.data.model.Article
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object JsonHelper {

    data class EducationData(
        val articles: List<Article>,
        val quick_tips: List<String>,
        val video_resources: List<VideoResource>
    )

    data class VideoResource(
        val title: String,
        val duration: String,
        val thumbnail: String
    )

    fun loadEducationData(context: Context): EducationData? {
        return try {
            val jsonString = context.assets.open("article_data.json")
                .bufferedReader()
                .use { it.readText() }

            val gson = Gson()
            val type = object : TypeToken<EducationData>() {}.type
            gson.fromJson(jsonString, type)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getAllArticles(context: Context): List<Article> {
        return loadEducationData(context)?.articles ?: emptyList()
    }

    fun getQuickTips(context: Context): List<String> {
        return loadEducationData(context)?.quick_tips ?: emptyList()
    }

    fun getVideoResources(context: Context): List<VideoResource> {
        return loadEducationData(context)?.video_resources ?: emptyList()
    }

    fun getArticleById(context: Context, id: Int): Article? {
        return getAllArticles(context).find { it.id == id }
    }

    fun getArticlesByCategory(context: Context, category: String): List<Article> {
        return getAllArticles(context).filter { it.category.equals(category, true) }
    }
}