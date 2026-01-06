package com.example.projekmobile_kel11.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.adapters.ArticleListAdapter
import com.example.projekmobile_kel11.databinding.ActivityCategoryBinding
import com.example.projekmobile_kel11.ui.education.detail.ArticleDetailActivity
import com.example.projekmobile_kel11.utils.JsonHelper

class CategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryBinding
    private lateinit var adapter: ArticleListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val category = intent.getStringExtra("category") ?: ""

        setupToolbar(category)
        setupRecyclerView()
        loadArticles(category)
    }

    private fun setupToolbar(category: String) {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Artikel $category"

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        adapter = ArticleListAdapter { article ->
            val intent = Intent(this, ArticleDetailActivity::class.java)
            intent.putExtra("article", article)
            startActivity(intent)
        }

        binding.rvArticles.layoutManager = LinearLayoutManager(this)
        binding.rvArticles.adapter = adapter
    }

    private fun loadArticles(category: String) {
        val articles = JsonHelper.getArticlesByCategory(this, category)
        adapter.submitList(articles)

        if (articles.isEmpty()) {
            binding.tvEmptyState.visibility = View.VISIBLE
            binding.rvArticles.visibility = View.GONE
        } else {
            binding.tvEmptyState.visibility = View.GONE
            binding.rvArticles.visibility = View.VISIBLE
        }
    }
}