package com.example.pdfviewer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.example.pdfviewer.databinding.ActivityMainBinding
import com.google.android.gms.instantapps.Launcher

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

        binding.floatingActionButton.setOnClickListener{

            launcher.launch("application/pdf")

        }
    }

}