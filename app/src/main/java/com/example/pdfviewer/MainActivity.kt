package com.example.pdfviewer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.pdfviewer.databinding.ActivityMainBinding
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener

class MainActivity : AppCompatActivity(), OnLoadCompleteListener, OnPageChangeListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var launcher: ActivityResultLauncher<String>
    private var isFabHidden = false
    private var currentPage: Int = 0
    private var pdfPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the background image
        val backgroundImage: ImageView = findViewById(R.id.imageView)
        // Set background image visibility to visible initially
        backgroundImage.visibility = View.VISIBLE

        val pdfView: PDFView = findViewById(R.id.pdfView)
        val selectedPageNumber = 2

        // Check if the activity was started with an intent
        if (intent.action == Intent.ACTION_VIEW && intent.type == "application/pdf") {
            val pdfUri = intent.data
            pdfUri?.let {
                openPdfFromUri(pdfView, it, selectedPageNumber)
                // Hide the background image when launching the PDF viewer
                backgroundImage.visibility = View.INVISIBLE
            }
        } else {
            // Handle other cases (e.g., opening PDF from internal storage, assets, etc.)
            pdfPath = "application.pdf"
            displayPdfPage(pdfView, pdfPath, selectedPageNumber)
        }

        // Initialize the ActivityResultLauncher
        launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                // Check if there is a saved current page from a previous instance
                currentPage = savedInstanceState?.getInt("currentPage", 0) ?: 0

                // Load PDF from URI
                openPdfFromUri(pdfView, it, currentPage)
                // Hide the background image when launching the PDF viewer
                backgroundImage.visibility = View.INVISIBLE
            }
        }

        binding.floatingActionButton.setOnClickListener {
            // Hide the background image when launching the PDF viewer
            backgroundImage.visibility = View.INVISIBLE
            launcher.launch("application/pdf")
        }
    }

    private fun openPdfFromUri(pdfView: PDFView, uri: Uri, pageNumber: Int) {
        pdfView.fromUri(uri)
            .defaultPage(pageNumber)
            .onLoad(this)
            .onPageChange(this)
            .load()
    }

    override fun loadComplete(numberOfPages: Int) {
        // PDF load is complete
    }

    override fun onPageChanged(page: Int, pageCount: Int) {
        if (page == pageCount - 1) {
            showFab()
        } else {
            hideFab()
        }

        val pageNumberText = "${page + 1} / $pageCount"
        binding.pageNumberTextView.text = pageNumberText
    }

    override fun onSaveInstanceState(outState: Bundle) {
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

    private fun displayPdfPage(pdfView: PDFView, pdfPath: String?, pageNumber: Int) {
        pdfPath?.let {
            try {
                pdfView.fromAsset(it)
                    .pages(pageNumber - 1)
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .defaultPage(pageNumber - 1)
                    .onLoad {
                        // Do something on PDF load, if needed
                    }
                    .load()
            } catch (e: Exception) {
                // Handle exception
                e.printStackTrace()
            }
        }
    }
}
