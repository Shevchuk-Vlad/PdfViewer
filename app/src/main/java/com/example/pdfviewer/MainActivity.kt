package com.example.pdfviewer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.pdfviewer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    private lateinit var binding : ActivityMainBinding

    private val launcher =   registerForActivityResult(
        ActivityResultContracts.GetContent()
    ){uri ->
        uri?.let {
            binding.pdfView.fromUri(it).load()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if the activity was started with an intent
        if (intent.action == Intent.ACTION_VIEW && intent.type == "application/pdf") {
            val pdfUri = intent.data
            pdfUri?.let {
                openPdfFromUri(it)
            }
        }

        binding.floatingActionButton.setOnClickListener {
            launcher.launch("application/pdf")
            // Add your logic to handle opening PDF files (e.g., launching a file picker)
        }
    }

    private fun openPdfFromUri(uri: Uri) {
        // Display the PDF using the provided URI
        binding.pdfView.fromUri(uri).load()
    }
}

