package com.example.newsappdemo.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsappdemo.R
import com.example.newsappdemo.adapters.NewsAdapter
import com.example.newsappdemo.databinding.FragmentFavoritesBinding
import com.example.newsappdemo.ui.NewsActivity
import com.example.newsappdemo.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class FavoritesFragment : Fragment(R.layout.fragment_favorites){
    lateinit var newsViewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    lateinit var binding: FragmentFavoritesBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding=FragmentFavoritesBinding.bind(view)
        newsViewModel=(activity as NewsActivity).newsViewModel
        setupFavouritesRecycler()
        newsAdapter.onItemClickListener {
            val bundle= Bundle().apply{
                putSerializable("article",it)
        }
            findNavController().navigate(R.id.action_favoritesFragment_to_articleFragment,bundle)
        }
        val itemTouchHelperCallback=object: ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position=viewHolder.adapterPosition
                val article=newsAdapter.differ.currentList[position]
                newsViewModel.deleteArticle(article)
                Snackbar.make(view,"Remove from Saved News",Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        newsViewModel.addToFavourites(article)
                    }
                }.show()
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).apply{
            attachToRecyclerView(binding.recyclerFavorites)
        }
        newsViewModel.getFavouriteNews().observe(viewLifecycleOwner, Observer {
            articles ->
            newsAdapter.differ.submitList(articles)
        })
    }
    fun setupFavouritesRecycler(){
        newsAdapter= NewsAdapter()
        binding.recyclerFavorites.apply {
            adapter=newsAdapter
            layoutManager=LinearLayoutManager(activity)
        }
    }
}