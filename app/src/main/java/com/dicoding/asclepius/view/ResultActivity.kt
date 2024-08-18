package com.dicoding.asclepius.view

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityResultBinding
import java.text.NumberFormat

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val resultImage = binding.resultImage
        val resultText = binding.resultText
        val percentageScore = binding.percentageScore

        val imageUriString = intent.getStringExtra("imageUri")
        val resultLabel = intent.getStringExtra("resultLabel")
        val resultScore = intent.getFloatExtra("resultScore", 0f)

        val imageUri = Uri.parse(imageUriString)

        resultImage.setImageURI(imageUri)

        resultText.text =
            buildString {
                append(NumberFormat.getPercentInstance().format(resultScore))
                append(" $resultLabel")
            }

        val scorePercentage = (resultScore * 100).toInt()
        percentageScore.progress = scorePercentage
    }


}