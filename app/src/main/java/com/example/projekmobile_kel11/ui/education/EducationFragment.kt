package com.example.projekmobile_kel11.ui.education

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.adapters.QuickTipAdapter
import com.example.projekmobile_kel11.data.model.Article
import com.example.projekmobile_kel11.databinding.FragmentEducationBinding
import com.example.projekmobile_kel11.ui.education.detail.ArticleDetailActivity
import com.example.projekmobile_kel11.utils.JsonHelper
import android.widget.TextView
import com.example.projekmobile_kel11.ui.CategoryActivity

class EducationFragment : Fragment() {

    private var _binding: FragmentEducationBinding? = null
    private val binding get() = _binding!!
    private lateinit var quickTipAdapter: QuickTipAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEducationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        loadData()
    }

    private fun setupRecyclerView() {
        quickTipAdapter = QuickTipAdapter()
        binding.rvQuickTips.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = quickTipAdapter
            setHasFixedSize(true)
        }
    }

    private fun loadData() {
        // Load quick tips
        val quickTips = JsonHelper.getQuickTips(requireContext())
        quickTipAdapter.submitList(quickTips)

        // Load articles (ambil 3 artikel pertama)
        val articles = JsonHelper.getAllArticles(requireContext())

        // Set artikel pertama
        if (articles.size >= 1) {
            binding.cardArticle1.setOnClickListener {
                openArticleDetail(articles[0])
            }
            binding.tvArticleTitle1.text = articles[0].title
        }

// Set artikel kedua
        if (articles.size >= 2) {
            binding.cardArticle2.setOnClickListener {
                openArticleDetail(articles[1])
            }
            binding.tvArticleTitle2.text = articles[1].title
        }

// Set artikel ketiga
        if (articles.size >= 3) {
            binding.cardArticle3.setOnClickListener {
                openArticleDetail(articles[2])
            }
            binding.tvArticleTitle3.text = articles[2].title
        }

    }

    private fun setupClickListeners() {
        // Kategori Pencegahan
        binding.cardCategoryPrevention.setOnClickListener {
            openCategory("Pencegahan")
        }

        // Kategori Gejala
        binding.cardCategorySymptoms.setOnClickListener {
            openCategory("Gejala")
        }
    }

    private fun openArticleDetail(article: Article) {
        val intent = Intent(requireContext(), ArticleDetailActivity::class.java)
        intent.putExtra("article", article)
        startActivity(intent)
    }

    private fun openCategory(category: String) {
        val articles = JsonHelper.getArticlesByCategory(requireContext(), category)
        if (articles.isNotEmpty()) {
            val intent = Intent(requireContext(), CategoryActivity::class.java)
            intent.putExtra("category", category)
            startActivity(intent)
        } else {
            // Tampilkan dialog bahwa belum ada artikel
            android.app.AlertDialog.Builder(requireContext())
                .setTitle("Kategori $category")
                .setMessage("Artikel untuk kategori ini sedang dalam pengembangan.")
                .setPositiveButton("OK", null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}