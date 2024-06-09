package com.example.newsappdemo.repository

import com.example.newsappdemo.api.RetrofitInstance
import com.example.newsappdemo.db.ArticleDatabase
import com.example.newsappdemo.models.Article
import retrofit2.Retrofit
import retrofit2.http.Query
import java.util.Locale.IsoCountryCode

class NewsRepository(val db:ArticleDatabase) {
    suspend fun getHeadlines(countryCode:String,pageNumber:Int)=RetrofitInstance.api.getHeadlines(countryCode,pageNumber)
    suspend fun searchNews(searchQuery:String,pageNumber:Int)=RetrofitInstance.api.SearchForNews(searchQuery,pageNumber)
    suspend fun upsert(article: Article)=db.getArticleDao().upsert(article)
    fun getFavouriteNews()=db.getArticleDao().getAllArticles()
    suspend fun deleteArticle(article: Article)=db.getArticleDao().deleteArticle(article)
}