package com.example.newsappdemo.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsappdemo.models.Article

@Dao
interface ArticleDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article):Long
    @Query("select * from articles")
    fun getAllArticles(): LiveData<List<Article>>
    @Delete
    suspend fun deleteArticle(article: Article)
}