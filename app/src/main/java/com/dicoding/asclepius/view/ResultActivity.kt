package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.dicoding.asclepius.NewsViewModel
import com.dicoding.asclepius.R
import com.dicoding.asclepius.adapter.NewsAdapter
import com.dicoding.asclepius.data.ResultData
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.Result
import com.dicoding.asclepius.helper.parcelable
import com.google.android.material.snackbar.Snackbar

class ResultActivity : AppCompatActivity() {

    private val viewModel: NewsViewModel by viewModels()
    private lateinit var binding: ActivityResultBinding
    private var imageUri: Uri? = null
    private var result: ResultData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO: Menampilkan hasil gambar, prediksi, dan confidence score.
        setupExtra()
        setUpActionBar()
        setupObserver()
        setupView()
    }

    private fun setupExtra() {
        val imageUriString = Uri.parse(intent.extras?.getString(EXTRA_IMAGE_URI))
        imageUri = imageUriString
        result = intent.parcelable(EXTRA_RESULT)
    }

    private fun setupObserver() = with(binding) {
        viewModel.newsResponse.observe(this@ResultActivity) {
            when (it) {
                is Result.Success -> {
                    showLoading(false)
                    val newsAdapter = NewsAdapter(it.data?.articles ?: listOf()) {
                        val intent = Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(it.url) }
                        startActivity(intent)
                    }
                    rvNews.adapter = newsAdapter
                }

                is Result.Loading -> showLoading(true)

                is Result.Error -> {
                    Snackbar.make(root, it.error, Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.understand)) { }.show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) = with(binding) {
        loadingBar.isVisible = isLoading
        rvNews.isVisible = !loadingBar.isVisible
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun setUpActionBar() = with(binding) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.app_name)
    }

    private fun setupView() = with(binding) {
        resultImage.setImageURI(imageUri)
        resultText.text = getString(R.string.app_confidence_result, result?.labelResult, result?.confidenceScore)
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_RESULT = "extra_result"
    }

}