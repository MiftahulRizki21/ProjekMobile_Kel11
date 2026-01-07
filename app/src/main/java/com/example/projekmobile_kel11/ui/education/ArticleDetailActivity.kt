package com.example.projekmobile_kel11.ui.education.detail

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.data.model.Article
import com.example.projekmobile_kel11.databinding.ActivityArticleDetailBinding
import com.google.android.material.appbar.MaterialToolbar

class ArticleDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArticleDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadArticle()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun loadArticle() {
        val article = intent.getParcelableExtra<Article>("article")

        if (article == null) {
            finish()
            return
        }

        binding.toolbar.title = article.title
        binding.tvArticleTitle.text = article.title
        binding.tvArticleCategory.text = article.category
        binding.tvReadTime.text = "${article.readTime} menit baca"

        val htmlContent = """
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
            <style>
                body {
                    font-family: sans-serif;
                    color: #212121;
                    line-height: 1.6;
                    padding: 0;
                }
                h2 { color: #1E88E5; }
                h3 { color: #1565C0; }
                b { color: #0D47A1; }
            </style>
        </head>
        <body>
            ${article.content}
        </body>
        </html>
    """.trimIndent()

        binding.webView.loadDataWithBaseURL(
            null,
            htmlContent,
            "text/html",
            "UTF-8",
            null
        )
    }

}