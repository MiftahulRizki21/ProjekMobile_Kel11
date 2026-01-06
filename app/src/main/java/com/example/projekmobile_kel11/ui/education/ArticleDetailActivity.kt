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
        val article = intent.getSerializableExtra("article") as? Article ?: return

        binding.toolbar.title = article.title
        binding.tvArticleTitle.text = article.title
        binding.tvArticleCategory.text = article.category
        binding.tvReadTime.text = "${article.readTime} menit baca"

        // Load HTML content
        val htmlContent = """
            <html>
            <head>
                <style>
                    body {
                        font-family: 'Roboto', sans-serif;
                        color: #212121;
                        line-height: 1.6;
                        padding: 16px;
                    }
                    h2 {
                        color: #1E88E5;
                        margin-top: 24px;
                        margin-bottom: 12px;
                    }
                    h3 {
                        color: #1565C0;
                        margin-top: 20px;
                        margin-bottom: 8px;
                    }
                    b {
                        color: #0D47A1;
                    }
                    ul {
                        padding-left: 20px;
                    }
                    li {
                        margin-bottom: 4px;
                    }
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