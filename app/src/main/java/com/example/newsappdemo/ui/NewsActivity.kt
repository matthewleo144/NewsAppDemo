package com.example.newsappdemo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.newsappdemo.R
import com.example.newsappdemo.databinding.ActivityNewsBinding
import com.example.newsappdemo.db.ArticleDatabase
import com.example.newsappdemo.models.Article
import com.example.newsappdemo.repository.NewsRepository
import com.example.newsappdemo.util.NewsViewModelProviderFactory

class NewsActivity : AppCompatActivity() {
    lateinit var newsViewModel: NewsViewModel
    lateinit var binding: ActivityNewsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val newsRepository=NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory=NewsViewModelProviderFactory(application,newsRepository)
        newsViewModel=ViewModelProvider(this,viewModelProviderFactory).get(NewsViewModel::class.java)
        val navHostFragment=supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        val navController=navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }
}