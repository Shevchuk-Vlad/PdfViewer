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
    private var currentPage: Int = 0 // Track the current page

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the ActivityResultLauncher
        launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                // Check if there is a saved current page from a previous instance
                if (savedInstanceState != null) {
                    currentPage = savedInstanceState.getInt("currentPage", 0)
                }

                binding.pdfView.fromUri(it)
                    .defaultPage(currentPage) // Set the default page
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
            .defaultPage(currentPage) // Set the default page
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

    override fun onSaveInstanceState(outState: Bundle) {
        // Save the current page when the activity is about to be destroyed
        outState.putInt("currentPage", binding.pdfView.currentPage)
        super.onSaveInstanceState(outState)
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

