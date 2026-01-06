package com.example.projekmobile_kel11.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.data.model.Article
import com.example.projekmobile_kel11.databinding.ItemArticleListBinding

class ArticleListAdapter(
    private val onItemClick: (Article) -> Unit
) : RecyclerView.Adapter<ArticleListAdapter.ArticleViewHolder>() {

    private val articles = mutableListOf<Article>()

    inner class ArticleViewHolder(private val binding: ItemArticleListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {
            binding.tvArticleTitle.text = article.title
            binding.tvArticleCategory.text = article.category
            binding.tvReadTime.text = "${article.readTime} menit baca"

            // Set background color berdasarkan kategori
            val bgColor = when (article.category) {
                "Kanker Mulut" -> R.drawable.bg_tip_orange
                "Perawatan Harian" -> R.drawable.bg_tip_green
                "Nutrisi" -> R.drawable.bg_tip_blue
                "Gejala" -> R.drawable.bg_tip_purple
                "Pencegahan" -> R.drawable.bg_category
                else -> R.drawable.bg_category
            }
            binding.tvArticleCategory.setBackgroundResource(bgColor)


            binding.root.setOnClickListener {
                onItemClick(article)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemArticleListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(articles[position])
    }

    override fun getItemCount(): Int = articles.size

    fun submitList(newArticles: List<Article>) {
        articles.clear()
        articles.addAll(newArticles)
        notifyDataSetChanged()
    }
}