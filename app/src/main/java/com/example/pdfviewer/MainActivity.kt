package com.example.pdfviewer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.pdfviewer.databinding.ActivityMainBinding
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener

class MainActivity : AppCompatActivity(), OnLoadCompleteListener, OnPageChangeListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var launcher: ActivityResultLauncher<String>
    private var isFabHidden = false // Flag to track the visibility of FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the ActivityResultLauncher
        launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                binding.pdfView.fromUri(it)
                    .onLoad(this) // Set load complete listener
                    .onPageChange(this) // Set page change listener
                    .load()

                // Hide the FloatingActionButton when opening the PDF
                hideFab()
            }
        }

        // Check if the activity was started with an intent
        if (intent.action == Intent.ACTION_VIEW && intent.type == "application/pdf") {
            val pdfUri = intent.data
            pdfUri?.let {
                openPdfFromUri(it)
            }
        }

        binding.floatingActionButton.setOnClickListener {
            launcher.launch("application/pdf")
        }
    }

    private fun openPdfFromUri(uri: Uri) {
        // Display the PDF using the provided URI
        binding.pdfView.fromUri(uri)
            .onLoad(this) // Set load complete listener
            .onPageChange(this) // Set page change listener
            .load()
    }

    override fun loadComplete(numberOfPages: Int) {
        // PDF load is complete
    }

    override fun onPageChanged(page: Int, pageCount: Int) {
        // Page in the PDF has changed
        if (page == pageCount - 1) {
            // Last page is reached, hide the FloatingActionButton
            showFab()
        } else {
            // Not on the last page, show the FloatingActionButton
            hideFab()
        }

        // Update page numbering
        val pageNumberText = "${page + 1} / $pageCount"
        binding.pageNumberTextView.text = pageNumberText
    }

    private fun hideFab() {
        if (!isFabHidden) {
            binding.floatingActionButton.hide()
            isFabHidden = true
        }
    }

    private fun showFab() {
        if (isFabHidden) {
            binding.floatingActionButton.show()
            isFabHidden = false
        }
    }
}
