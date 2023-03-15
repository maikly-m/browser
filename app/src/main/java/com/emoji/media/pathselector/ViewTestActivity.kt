package com.emoji.media.pathselector

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.emoji.media.pathselector.databinding.ActivityViewTestBinding

class ViewTestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnZoomIn.setOnClickListener {
            val a = binding.bae.zoomIn()
            binding.textZoom.text = "${a}"
        }
        binding.btnZoomOut.setOnClickListener {
            val a = binding.bae.zoomOut()
            binding.textZoom.text = "${a}"
        }
    }
}