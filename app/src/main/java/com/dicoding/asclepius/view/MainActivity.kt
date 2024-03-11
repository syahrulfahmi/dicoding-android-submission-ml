package com.dicoding.asclepius.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.google.android.material.snackbar.Snackbar
import org.tensorflow.lite.task.vision.classifier.Classifications

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentImageUri: Uri? = null
    private lateinit var imageClassifierHelper: ImageClassifierHelper

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Snackbar.make(binding.root, "Permission request granted", Snackbar.LENGTH_SHORT).show()
        } else {
            Snackbar.make(binding.root, "Permission request denied", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        this, REQUIRED_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED

    private val launcherGallery =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                currentImageUri = uri
                showImage()
            } else {
                Snackbar.make(binding.root, "No Media Selected", Snackbar.LENGTH_SHORT).show()
            }
        }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPermission()
        setupListener()
    }

    private fun setupPermission() {
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
        binding.galleryButton.setOnClickListener { startGallery() }
    }

    private fun startGallery() {
        // TODO: Mendapatkan gambar dari Gallery.
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showImage() {
        // TODO: Menampilkan gambar sesuai Gallery yang dipilih.
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun setupListener() = with(binding) {
        analyzeButton.setOnClickListener {
            analyzeImage()
        }
    }

    private fun analyzeImage() {
        // TODO: Menganalisa gambar yang berhasil ditampilkan.
        imageClassifierHelper = ImageClassifierHelper(
            this,
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onResult(result: List<Classifications>?, inferenceTime: Long) {
                    runOnUiThread {
                        result?.let {
                            if (it.isNotEmpty() && it[0].categories.isNotEmpty()) {
                                println(it)
                            }
                        }
                    }
                }

                override fun onError(error: String) {
                    runOnUiThread {
                        Snackbar.make(binding.root, error, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        )

//        val intent = Intent(this, ResultActivity::class.java)
//        intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, currentImageUri.toString())
//        startActivity(intent)
    }

    private fun moveToResult() {
        val intent = Intent(this, ResultActivity::class.java)
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}