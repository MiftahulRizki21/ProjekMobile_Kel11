package com.example.projekmobile_kel11.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Article(
    val id: Int = 0,
    val title: String = "",
    val category: String = "",
    val readTime: Int = 0,
    val imageRes: String = "", // nama drawable
    val content: String = ""
) : Parcelable