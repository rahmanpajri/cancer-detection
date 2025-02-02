package com.dicoding.asclepius.view

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.google.mlkit.vision.common.InputImage
import org.tensorflow.lite.task.vision.classifier.Classifications

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var imageClassifierHelper: ImageClassifierHelper

    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.analyzeButton.setOnClickListener {
            currentImageUri?.let {
                analyzeImage(it)
            } ?: run {
                showToast(getString(R.string.insert_please))
            }
        }

        imageClassifierHelper = ImageClassifierHelper(
            threshold = 0.1f,
            maxResults = 3,
            modelName = "cancer_classification.tflite",
            context = this,
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    showToast(error)
                }

                override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                    runOnUiThread {
                        results?.let { classificationsList ->
                            if(classificationsList.isNotEmpty() && classificationsList[0].categories.isNotEmpty()){
                                println(classificationsList)
                                classificationsList[0].categories.maxByOrNull { it.score }!!.let {
                                    val imageUri = currentImageUri
                                    val label = it.label
                                    val score = it.score
                                    if (imageUri != null) {
                                        moveToResult(imageUri, label, score)
                                    }
                                }
                            }else{
                                showToast(getString(R.string.insert_please))
                            }
                        }
                    }
                }
            }
        )
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Launcher", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun analyzeImage(uri: Uri) {
        imageClassifierHelper.classifyStaticImage(uri)
    }

    private fun moveToResult(imageUri: Uri, resultLabel: String, resultScore: Float) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("imageUri", imageUri.toString())
        intent.putExtra("resultLabel", resultLabel)
        intent.putExtra("resultScore", resultScore)
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}