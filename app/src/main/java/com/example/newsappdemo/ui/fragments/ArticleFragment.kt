package com.example.newsappdemo.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.newsappdemo.R
import com.example.newsappdemo.databinding.FragmentArticleBinding
import com.example.newsappdemo.ui.NewsActivity
import com.example.newsappdemo.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class ArticleFragment : Fragment(R.layout.fragment_article) {
    lateinit var newsViewModel: NewsViewModel
    val args: ArticleFragmentArgs by navArgs()
    lateinit var binding: FragmentArticleBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentArticleBinding.bind(view)
        newsViewModel = (activity as NewsActivity).newsViewModel
        val article = args.article
        binding.webView.apply {
            webViewClient = WebViewClient()
            article!!.url?.let {
                Log.d("Err", "Enter in Article fragment")
                loadUrl(it)
            }
        }

        // Existing bookmark button functionality
        binding.fab.setOnClickListener {
            if (article != null) {
                newsViewModel.addToFavourites(article)
            }
            Snackbar.make(view, "Saved", Snackbar.LENGTH_LONG).show()
        }

        // New share button functionality
        binding.share.setOnClickListener {
            if (article != null) {
                shareArticle(article.title, article.url)
            }
        }
    }

    private fun shareArticle(title: String?, url: String?) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "$title\n\nRead more at: $url")
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Share Article via"))
    }
}