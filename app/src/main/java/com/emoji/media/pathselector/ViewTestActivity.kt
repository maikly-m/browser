package com.emoji.media.pathselector

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.emoji.media.pathselector.databinding.ActivityViewTestBinding

class ViewTestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewTestBinding
    private var i = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnZoomIn.setOnClickListener {
            i++
            val a = binding.bae.changeScale(i)
            binding.textZoom.text = "${a}"
        }
        binding.btnZoomOut.setOnClickListener {
            i--
            val a = binding.bae.changeScale(i)
            binding.textZoom.text = "${a}"
        }
    }
}