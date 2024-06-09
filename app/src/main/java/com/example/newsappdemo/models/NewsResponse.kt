package com.example.newsappdemo.models

import com.example.newsappdemo.models.Article

data class NewsResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)