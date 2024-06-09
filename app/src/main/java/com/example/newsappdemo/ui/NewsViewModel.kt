package com.example.newsappdemo.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.newsappdemo.models.Article
import com.example.newsappdemo.models.NewsResponse
import com.example.newsappdemo.repository.NewsRepository
import com.example.newsappdemo.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import java.util.Locale.IsoCountryCode

class NewsViewModel(app:Application,val newsRepository: NewsRepository) :AndroidViewModel(app){
    val headlines:MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var headlinePage=1
    var headlineResponse:NewsResponse?=null
    var searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()

    var searchNewsPage=1
    var searchNewsResponse:NewsResponse?=null
    var newSearchQuery:String?=null
    var oldSearchQuery:String?=null
    init {
        getHeadlines("us")
    }
    private fun headlinesResponse(response: Response<NewsResponse>):Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let {
                resultResponse ->
                headlinePage++
                if (headlineResponse==null){
                    headlineResponse=resultResponse
                }
                else {
                    val oldArticles=headlineResponse?.articles
                    val newArticles=resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(headlineResponse?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
    private fun handleSearchNewsResponse(response: Response<NewsResponse>):Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let{
                resultResponse ->
                if(searchNewsResponse == null || newSearchQuery!=oldSearchQuery){
                    searchNewsPage=1
                    oldSearchQuery=newSearchQuery
                    searchNewsResponse=resultResponse
                }
                else{
                    searchNewsPage++
                    val oldArticles=searchNewsResponse?.articles
                    val newArticles=resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse?:resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
    fun getHeadlines(countryCode:String)=viewModelScope.launch {
      headlineInternet(countryCode)
    }
    fun searchNews(searchQuery: String)=viewModelScope.launch {
        searchNewsInternet(searchQuery)
    }
    fun addToFavourites(article: Article)=viewModelScope.launch {
        newsRepository.upsert(article)
    }
    fun getFavouriteNews()=newsRepository.getFavouriteNews()
    fun deleteArticle(article: Article)=viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }
    fun internetConnection(context: Context):Boolean{
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
            return getNetworkCapabilities(activeNetwork)?.run {
                when{
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ->true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ->true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ->true
                    else -> false
                }

            }?:false
        }
    }
    private suspend fun headlineInternet(countryCode: String){
        headlines.postValue(Resource.Loading())
        try{
            if(internetConnection(this.getApplication())){
                val response=newsRepository.getHeadlines(countryCode,headlinePage)
                headlines.postValue(headlinesResponse(response))
            }
            else{
                headlines.postValue(Resource.Error("No Internet connection"))
            }
        }catch (t:Throwable){
            when(t){
                is IOException -> headlines.postValue(Resource.Error("Unable to reach"))
                else -> headlines.postValue(Resource.Error("No Signal"))
            }
        }
    }
    private suspend fun searchNewsInternet(searchQuery:String){
        newSearchQuery=searchQuery
        searchNews.postValue(Resource.Loading())
        try {
            if (internetConnection(this.getApplication())){
                val response=newsRepository.searchNews(searchQuery,searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            }else{
                searchNews.postValue(Resource.Error("No Internet connection"))
            }

        }
        catch (t:Throwable){
            when(t){
                is IOException -> searchNews.postValue(Resource.Error("Unable to reach"))
                else -> searchNews.postValue(Resource.Error("No Signal"))
            }

        }
    }
}